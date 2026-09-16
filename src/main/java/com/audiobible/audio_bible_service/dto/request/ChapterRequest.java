package com.audiobible.audio_bible_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapterRequest {
    private int id;
    private int bookId;
    private String reader;
     private String title;
    private String audioUrl;
    private String duration;
}
