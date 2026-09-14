package com.audiobible.audio_bible_service.dto.request;

import com.audiobible.audio_bible_service.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRoleUpdateRequest {
    @NotNull(message = "Vai trò không được để trống")
    private Role role;
}