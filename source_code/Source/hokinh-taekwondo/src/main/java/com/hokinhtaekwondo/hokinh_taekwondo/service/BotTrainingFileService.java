package com.hokinhtaekwondo.hokinh_taekwondo.service;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.bot.FileDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.bot.PythonFileResponse;
import com.hokinhtaekwondo.hokinh_taekwondo.dto.bot.PythonUploadResponse;
import com.hokinhtaekwondo.hokinh_taekwondo.model.BotFile;
import com.hokinhtaekwondo.hokinh_taekwondo.repository.BotFileRepository;
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
import java.time.LocalDateTime;
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
            if (pythonFiles != null) {
                for (PythonFileResponse pythonFile : pythonFiles) {
                    System.out.println(pythonFile.getName());
                    BotFile file = fileDatabase.findBotFileByPythonFileId(pythonFile.getId()).orElse(null);
                    if(file == null) {
                        file = new BotFile();
                        file.setName(pythonFile.getName());
                        file.setSize(pythonFile.getSize());
                        file.setUploadDate(pythonFile.getUploadDate());
                        file.setType(pythonFile.getType());
                        file.setStatus(pythonFile.getStatus());
                        file.setPythonFileId(pythonFile.getId());

                        fileDatabase.save(file);
                    }
                }
            }
            System.out.println("pt: " + pythonFiles);
        } catch (Exception e) {
            System.err.println("Error syncing: " + e.getMessage());
        }

        return fileDatabase.findAll().stream().map(this::toFileDTO).toList();
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
                throw new RuntimeException("Failed to upload file to Python backend");
            }

            // Create FileDTO
            BotFile createdFile = new BotFile();
            createdFile.setName(file.getOriginalFilename());
            createdFile.setSize(formatFileSize(file.getSize()));
            createdFile.setUploadDate(LocalDateTime.now().toString().split("T")[0]);
            createdFile.setStatus("processing");
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

    public FileDTO reindexFile(String fileId) {
        BotFile file = fileDatabase.findById(fileId).orElse(null);
        if (file != null) {
            // Trigger reindexing in Python backend
            try {
                restTemplate.postForEntity(
                        pythonAiUrl + "/vector-store/files/" + file.getPythonFileId() + "/reindex",
                        null,
                        Map.class
                );

                file.setStatus("processing");
            } catch (Exception e) {
                file.setStatus("error");
            }
        }
        else {
            return null;
        }
        return toFileDTO(file);
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
