package com.traderbuddy.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    // This will resolve to your project root + /uploads/
    private static final String UPLOAD_DIR = "uploads";

    @Value("${server.port:8080}")
    private String serverPort;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) throws IOException {
        // Ensure folder exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFileName;

        // Build full path
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Set file permission: readable by all, writable by app (Windows limited support)
        File savedFile = filePath.toFile();
        savedFile.setReadable(true, false); // readable by everyone
        savedFile.setWritable(true, true);  // writable by owner (Spring app user)

        // Construct public URL (e.g., http://localhost:8080/uploads/filename.jpg)
        String fileUrl = "http://localhost:" + serverPort + "/uploads/" + fileName;

        return ResponseEntity.ok(fileUrl);
    }
}
