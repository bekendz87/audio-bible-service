package com.audiobible.audio_bible_service.dto.response;

import com.audiobible.audio_bible_service.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String fullName;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
}