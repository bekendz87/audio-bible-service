package com.audiobible.audio_bible_service.controller;

import com.audiobible.audio_bible_service.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/audio")
    public ResponseEntity<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) {
        String dbPath = fileStorageService.storeFile(file, "audio");
        return ResponseEntity.ok(Map.of("path", dbPath));
    }

    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String dbPath = fileStorageService.storeFile(file, "images");
        return ResponseEntity.ok(Map.of("path", dbPath));
    }
}