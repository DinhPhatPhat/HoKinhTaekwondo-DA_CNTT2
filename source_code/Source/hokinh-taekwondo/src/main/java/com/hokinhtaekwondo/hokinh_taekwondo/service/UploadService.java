package com.hokinhtaekwondo.hokinh_taekwondo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class UploadService {

    public static String upLoadImage(MultipartFile imageFile, String id, String uploadDir, String accessPath) throws IOException {

        //Take file extension
        String fileExtension = ".webp";

        //File name: <story id>.<file extension> (Ex: 1..webp)
        String fileName = id + fileExtension;
        //Upload path
        File uploadPath = new File(uploadDir);

        // Make direct path if not exists
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        // Save file into path
        Path destination = Path.of(uploadDir + fileName);

        Files.copy(imageFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return accessPath + fileName;
    }
}
