package com.audiobible.audio_bible_service.controller;

import com.audiobible.audio_bible_service.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StorageController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{encryptedPath}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String encryptedPath,
            HttpServletRequest request) {
        try {
            Path filePath = fileStorageService.loadFileAsResource(encryptedPath);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = request.getServletContext().getMimeType(filePath.toFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}