package com.audiobible.audio_bible_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookResponse {
    private Integer id;
    private String title;
    private String subtitle;
    private String artworkUrl;
    private String testament;
    private Integer totalChapters;
    private Integer categoryId;
    private String categoryName;
}