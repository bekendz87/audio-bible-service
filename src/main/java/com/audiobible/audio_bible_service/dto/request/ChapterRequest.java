package com.audiobible.audio_bible_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapterRequest {
    private Integer id;
    @NotBlank(message = "Mã sách không được để trống")
    private Integer bookId;
    @NotNull(message = "Số chương không được để trống")
    private Integer chapterNum;
    private String reader;
    private String audioUrl;
    private String duration;
}
