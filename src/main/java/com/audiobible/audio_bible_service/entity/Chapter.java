package com.audiobible.audio_bible_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chapters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {

    @Id
    private String id; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer chapterNum;

    private String reader; 

    @Column(columnDefinition = "TEXT")
    private String audioUrl;

    private String duration; 
}