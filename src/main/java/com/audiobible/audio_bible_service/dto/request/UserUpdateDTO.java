package com.audiobible.audio_bible_service.dto.request;

import com.audiobible.audio_bible_service.entity.Role;
import lombok.*;


@Data
public class UserUpdateDTO {
    private String fullName;
    private String avatarUrl;
    private Role role;
    private String oldPassword;
    private String newPassword;

}