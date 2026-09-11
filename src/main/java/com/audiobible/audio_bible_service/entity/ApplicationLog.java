package com.audiobible.audio_bible_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "application_logs", indexes = {
        @Index(name = "idx_application_logs_created_at", columnList = "createdAt"),
        @Index(name = "idx_application_logs_request_id", columnList = "requestId"),
        @Index(name = "idx_application_logs_type", columnList = "logType")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private LogType logType;

    @Column(nullable = false, length = 16)
    private String level;

    @Column(length = 100)
    private String requestId;

    @Column(length = 10)
    private String httpMethod;

    @Column(length = 500)
    private String path;

    @Column(columnDefinition = "TEXT")
    private String queryString;

    @Column(length = 100)
    private String remoteAddress;

    private Integer httpStatus;
    private Long durationMs;

    @Column(length = 255)
    private String contentType;

    @Column(columnDefinition = "LONGTEXT")
    private String requestBody;

    @Column(columnDefinition = "LONGTEXT")
    private String responseBody;

    @Column(length = 255)
    private String exceptionClass;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "LONGTEXT")
    private String stackTrace;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
    }
}
