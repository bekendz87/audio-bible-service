package com.audiobible.audio_bible_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgressUpdateRequest {
    @NotNull
    private String chapterId;

    @NotNull
    private Integer lastPositionSeconds;

    private Boolean isCompleted;
}