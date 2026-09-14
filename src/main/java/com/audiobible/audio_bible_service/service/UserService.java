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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.audiobible.audio_bible_service.dto.response.UserResponse;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final UserProgressRepository userProgressRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    public UserResponse updateRole(int id, Role role) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User không tồn tại"));
        user.setRole(role);
        return toResponse(userRepository.save(user));
    }

    public UserResponse setActive(int id, boolean active) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User không tồn tại"));
        user.setActive(active);
        return toResponse(userRepository.save(user));
    }

    public void deleteUser(int id) {
        if (!userRepository.existsById(id))
            throw new RuntimeException("User không tồn tại");
        userRepository.deleteById(id);
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder().id(u.getId()).email(u.getEmail()).fullName(u.getFullName())
                .role(u.getRole()).active(u.getActive()).createdAt(u.getCreatedAt()).build();
    }

    public boolean isUserEmail(int userId, String email) {
        return userRepository.findById(userId).map(user -> user.getEmail().equalsIgnoreCase(email)).orElse(false);
    }

    public void updateProgress(int userId, ProgressUpdateRequest request) {
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