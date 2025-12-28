package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.bot.FileDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.bot.PythonFileResponse;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.bot.PythonUploadResponse;
import com.hokinhtaekwondo.hokinh_taekwondo.model.BotFile;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.BotFileRepository;
import com.hokinhtaekwondo.hokinh_taekwondo.utils.time.VietNamTime;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class BotTrainingFileService {

    @Value("${python.ai.url:http://localhost:8000}")
    private String pythonAiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BotFileRepository fileDatabase; // Replace with actual DB

    @Transactional
    public List<FileDTO> getAllFiles() {
        try {
            ResponseEntity<List<PythonFileResponse>> response = restTemplate.exchange(
                    pythonAiUrl + "/vector-store/files",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PythonFileResponse>>() {}
            );

            List<PythonFileResponse> pythonFiles = response.getBody();
            if(pythonFiles == null || pythonFiles.isEmpty()) {
                return fileDatabase.findAll()
                        .stream()
                        .map(this::toFileDTO).toList();
            }

            HashMap<String, PythonFileResponse> pythonFilesById = toHashMapVectorFile(pythonFiles);
            List<String> pythonFileIds = pythonFilesById.keySet().stream().toList();
            List<BotFile> existedFiles = fileDatabase.findBotFilesByPythonFileIdIn(pythonFileIds);
            HashMap<String, BotFile> botFilesById = toHashMapBotFile(existedFiles);

            for (BotFile file : existedFiles) {
                if (file.getStatus().equals("STATE_PENDING")) {
                    file.setStatus(pythonFilesById.get(file.getPythonFileId()).getStatus());
                }
            }
            fileDatabase.deleteAllByPythonFileIdNotInOrPythonFileIdNull(pythonFileIds);
            for(PythonFileResponse pythonFile : pythonFiles) {
                if(botFilesById.get(pythonFile.getId()) == null) {
                    BotFile botFile = new BotFile();
                    botFile.setPythonFileId(pythonFile.getId());
                    botFile.setStatus(pythonFile.getStatus());
                    botFile.setType(pythonFile.getType());
                    botFile.setSize(pythonFile.getSize());
                    botFile.setName(pythonFile.getName());
                    botFile.setUploadDate(VietNamTime.fromUtcIsoToVietnam(pythonFile.getUploadDate()));
                    fileDatabase.save(botFile);
                }
            }

            System.out.println("pt: " + pythonFiles);
        } catch (Exception e) {
            System.err.println("Error syncing: " + e.getMessage());
        }

        return fileDatabase.findAll().stream().map(this::toFileDTO).toList();
    }

    private HashMap<String, PythonFileResponse> toHashMapVectorFile(List<PythonFileResponse> files) {
        HashMap<String, PythonFileResponse> result = new HashMap<>();
        for (PythonFileResponse file : files) {
            result.put(file.getId(), file);
        }
        return result;
    }

    private HashMap<String, BotFile> toHashMapBotFile(List<BotFile> files) {
        HashMap<String, BotFile> result = new HashMap<>();
        for (BotFile file : files) {
            result.put(file.getPythonFileId(), file);
        }
        return result;
    }

    private FileDTO toFileDTO(BotFile file) {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(file.getId());
        fileDTO.setName(file.getName());
        fileDTO.setSize(file.getSize());
        fileDTO.setUploadDate(file.getUploadDate());
        fileDTO.setType(file.getType());
        fileDTO.setStatus(file.getStatus());
        fileDTO.setPythonFileId(file.getPythonFileId());
        return fileDTO;
    }

    public FileDTO getFileById(String fileId) {
        BotFile file = fileDatabase.findById(fileId).orElseThrow(() -> new RuntimeException("Không tìm thấy file"));
        return toFileDTO(file);
    }

    @Transactional
    public FileDTO uploadFile(MultipartFile file) throws IOException {
        File tempFile = convertMultipartFileToFile(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(tempFile));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<PythonUploadResponse> response = restTemplate.postForEntity(
                    pythonAiUrl + "/vector-store/upload",
                    requestEntity,
                    PythonUploadResponse.class  // Type-safe!
            );

            PythonUploadResponse uploadResponse = response.getBody();
            if (uploadResponse == null || !uploadResponse.isSuccess()) {
                throw new RuntimeException("Đã xảy ra lỗi khi upload file. Vui lòng thử lại");
            }
            // Create FileDTO
            BotFile createdFile = new BotFile();
            createdFile.setName(file.getOriginalFilename());
            createdFile.setSize(formatFileSize(file.getSize()));
            createdFile.setUploadDate(VietNamTime.nowDateTime().toString());
            createdFile.setStatus(uploadResponse.getStatus());
            createdFile.setType(getFileExtension(Objects.requireNonNull(file.getOriginalFilename())));
            createdFile.setPythonFileId(uploadResponse.getFileId());  // Type-safe access!

            return toFileDTO(fileDatabase.save(createdFile));
        } catch (Exception e) {
            System.err.println("Error syncing: " + e.getMessage());
        }
        finally {
            if (tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    System.err.println("Cảnh báo: Không thể xóa file tạm thời: " + tempFile.getAbsolutePath());
                }
            }
        }
        return null;
    }

    public void deleteFile(String fileId) {
        BotFile file = fileDatabase.findById(fileId).orElse(null);
        if (file != null) {
            // Delete from Python backend
            try {
                restTemplate.delete(
                        pythonAiUrl + "/vector-store/files/" + file.getPythonFileId()
                );
            } catch (Exception e) {
                // Handle error
                System.err.println("Error syncing: " + e.getMessage());
            }
            fileDatabase.deleteById(fileId);
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        return file;
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        return String.format("%.2f MB", size / (1024.0 * 1024.0));
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
