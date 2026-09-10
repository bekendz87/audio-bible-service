package com.audiobible.audio_bible_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    private String id;
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String testament;

    private Integer totalChapters;
    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String artworkUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}