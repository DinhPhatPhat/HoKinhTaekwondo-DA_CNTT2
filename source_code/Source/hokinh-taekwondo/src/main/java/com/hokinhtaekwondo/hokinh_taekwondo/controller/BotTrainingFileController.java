package com.hokinhtaekwondo.hokinh_taekwondo.controller;

import com.hokinhtaekwondo.hokinh_taekwondo.dto.bot.FileDTO;
import com.hokinhtaekwondo.hokinh_taekwondo.service.BotTrainingFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/bot-files")
public class BotTrainingFileController {

    @Autowired
    private BotTrainingFileService botTrainingFileService;

    // Get all files
    @GetMapping
    public ResponseEntity<List<FileDTO>> getAllFiles() {
        return ResponseEntity.ok(botTrainingFileService.getAllFiles());
    }

    // Get file by ID
    @GetMapping("/{fileId}")
    public ResponseEntity<FileDTO> getFile(@PathVariable String fileId) {
        return ResponseEntity.ok(botTrainingFileService.getFileById(fileId));
    }

    // Upload file
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {

        try {
            FileDTO uploadedFile = botTrainingFileService.uploadFile(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Delete file
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileId) {
        botTrainingFileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

    // Reindex file
    @PostMapping("/{fileId}/reindex")
    public ResponseEntity<FileDTO> reindexFile(@PathVariable String fileId) {
        FileDTO reindexedFile = botTrainingFileService.reindexFile(fileId);
        return ResponseEntity.ok(reindexedFile);
    }
}
