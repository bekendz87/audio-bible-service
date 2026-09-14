package com.audiobible.audio_bible_service.repository;

import com.audiobible.audio_bible_service.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {
    Optional<UserProgress> findByUserIdAndChapterId(int userId, int chapterId);
}