package com.audiobible.audio_bible_service.repository;

import com.audiobible.audio_bible_service.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

    List<Chapter> findByBookIdAndIsRemoveFalseOrIsRemoveIsNull(int bookId);

    Page<Chapter> findByBookIdAndIsRemoveFalseOrIsRemoveIsNull(int bookId, Pageable pageable);

    Optional<Chapter> findFirstByIsRemoveFalseOrIsRemoveIsNullOrderByIdAsc();

    @Query("SELECT c FROM Chapter c WHERE (c.isRemove = false OR c.isRemove IS NULL) " +
            "AND c.audioUrl IS NOT NULL AND TRIM(c.audioUrl) <> '' ORDER BY c.id ASC")
    List<Chapter> findFirstWithAudio(Pageable pageable);

    @Query("SELECT c FROM Chapter c WHERE c.book.id = :bookId " +
            "AND (c.isRemove = false OR c.isRemove IS NULL) " +
            "AND c.audioUrl IS NOT NULL AND TRIM(c.audioUrl) <> ''")
    Page<Chapter> findByBookIdWithAudio(@Param("bookId") Integer bookId, Pageable pageable);
}