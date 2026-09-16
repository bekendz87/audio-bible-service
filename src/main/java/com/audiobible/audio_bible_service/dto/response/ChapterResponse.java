package com.audiobible.audio_bible_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChapterResponse {
    private Integer id;
    private Integer bookId;
    private String bookName;
    private String reader;
    private String title;
    private Boolean remove;
    private String audioUrl;
    private String duration;
}