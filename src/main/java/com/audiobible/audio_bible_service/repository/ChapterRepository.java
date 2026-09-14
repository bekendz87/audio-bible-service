package com.audiobible.audio_bible_service.repository;

import com.audiobible.audio_bible_service.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    List<Chapter> findByBookIdOrderByChapterNumAsc(int bookId);

    Page<Chapter> findByBookId(int bookId, Pageable pageable);
}