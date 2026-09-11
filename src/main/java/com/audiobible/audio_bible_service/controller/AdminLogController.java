package com.audiobible.audio_bible_service.controller;

import com.audiobible.audio_bible_service.logging.LogManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import com.audiobible.audio_bible_service.config.AdminApiController;
import com.audiobible.audio_bible_service.entity.ApplicationLog;
import com.audiobible.audio_bible_service.entity.LogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController
@AdminApiController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class AdminLogController {
    private final LogManagementService logService;

    @GetMapping
    public List<String> list() throws IOException {
        return logService.listLogFiles();
    }

    @GetMapping("/database")
    public Page<ApplicationLog> databaseLogs(@RequestParam(required = false) LogType type,
                                             @RequestParam(required = false) String requestId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "50") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 200);
        return logService.find(type, requestId,
                PageRequest.of(safePage, safeSize, Sort.by("createdAt").descending()));
    }

    @DeleteMapping("/database")
    public ResponseEntity<Void> clearDatabase() {
        logService.clearDatabaseLogs();
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{fileName}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> read(@PathVariable String fileName,
                                       @RequestParam(defaultValue = "500") int lines) throws IOException {
        return ResponseEntity.ok(logService.read(fileName, lines));
    }

    @DeleteMapping("/current")
    public ResponseEntity<Void> clearCurrent() throws IOException {
        logService.clearCurrentLog();
        return ResponseEntity.noContent().build();
    }
}
