package com.audiobible.audio_bible_service.controller;

import com.audiobible.audio_bible_service.dto.request.ProgressUpdateRequest;
import com.audiobible.audio_bible_service.dto.request.RegisterRequest;
import com.audiobible.audio_bible_service.entity.User;
import com.audiobible.audio_bible_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/{userId}/progress")
    public ResponseEntity<String> updateProgress(
            @PathVariable String userId,
            @Valid @RequestBody ProgressUpdateRequest request) {
        userService.updateProgress(userId, request);
        return ResponseEntity.ok("Cập nhật tiến trình thành công");
    }
}