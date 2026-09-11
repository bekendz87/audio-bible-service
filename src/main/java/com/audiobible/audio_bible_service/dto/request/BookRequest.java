package com.audiobible.audio_bible_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookRequest {
    private String id;
    @NotBlank(message = "Tên sách không được để trống")
    private String title;
    @NotBlank(message = "Kinh điển không được để trống")
    private String testament;
    private Integer totalChapters;
    private String subtitle;
    private String artworkUrl;
    private String categoryId;
}
