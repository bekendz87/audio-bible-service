package com.audiobible.audio_bible_service.repository;

import com.audiobible.audio_bible_service.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

    List<Chapter> findByBookIdAndIsRemoveFalseOrIsRemoveIsNull(int bookId);

    Page<Chapter> findByBookIdAndIsRemoveFalseOrIsRemoveIsNull(int bookId, Pageable pageable);

    Optional<Chapter> findFirstByIsRemoveFalseOrIsRemoveIsNullOrderByIdAsc();
}