package com.audiobible.audio_bible_service.service;

import com.audiobible.audio_bible_service.dto.request.ProgressUpdateRequest;
import com.audiobible.audio_bible_service.dto.request.RegisterRequest;
import com.audiobible.audio_bible_service.entity.Chapter;
import com.audiobible.audio_bible_service.entity.Role;
import com.audiobible.audio_bible_service.entity.User;
import com.audiobible.audio_bible_service.entity.UserProgress;
import com.audiobible.audio_bible_service.repository.ChapterRepository;
import com.audiobible.audio_bible_service.repository.UserProgressRepository;
import com.audiobible.audio_bible_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final UserProgressRepository userProgressRepository;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(hashPassword(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    private String hashPassword(String password) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder encoded = new StringBuilder(hash.length * 2);
            for (byte value : hash) {
                encoded.append(String.format("%02x", value));
            }
            return encoded.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Không thể mã hóa mật khẩu", exception);
        }
    }

    public void updateProgress(String userId, ProgressUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chương không tồn tại"));

        UserProgress progress = userProgressRepository
                .findByUserIdAndChapterId(userId, request.getChapterId())
                .orElse(UserProgress.builder()
                        .user(user)
                        .chapter(chapter)
                        .build());

        progress.setLastPositionSeconds(request.getLastPositionSeconds());
        progress.setIsCompleted(request.getIsCompleted() != null ? request.getIsCompleted() : false);

        userProgressRepository.save(progress);
    }
}