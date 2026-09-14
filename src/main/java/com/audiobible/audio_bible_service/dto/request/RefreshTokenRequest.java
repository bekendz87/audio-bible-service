package com.audiobible.audio_bible_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Mã truy cập không được để trống")
    private String refreshToken;
}
