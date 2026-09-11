package com.audiobible.audio_bible_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String email;
    private String role;
}
