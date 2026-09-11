package com.audiobible.audio_bible_service.repository;

import com.audiobible.audio_bible_service.entity.ApplicationLog;
import com.audiobible.audio_bible_service.entity.LogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationLogRepository extends JpaRepository<ApplicationLog, String> {
    Page<ApplicationLog> findByLogType(LogType logType, Pageable pageable);
    Page<ApplicationLog> findByRequestId(String requestId, Pageable pageable);
}
