package com.audiobible.audio_bible_service.repository;

import com.audiobible.audio_bible_service.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByTestament(String testament);

    Page<Book> findByTestamentIgnoreCase(String testament, Pageable pageable);
}