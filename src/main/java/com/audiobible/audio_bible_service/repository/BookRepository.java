package com.audiobible.audio_bible_service.repository;

import com.audiobible.audio_bible_service.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @EntityGraph(attributePaths = { "category" })
    List<Book> findByTestamentIgnoreCaseAndIsRemoveFalseOrIsRemoveIsNull(String testament);

    @EntityGraph(attributePaths = { "category" })
    Page<Book> findByTestamentIgnoreCaseAndIsRemoveFalseOrIsRemoveIsNull(String testament, Pageable pageable);

    @EntityGraph(attributePaths = { "category" })
    List<Book> findByIsRemoveFalseOrIsRemoveIsNull();

    @Query(value = "SELECT b FROM Book b LEFT JOIN FETCH b.category WHERE b.isRemove IS NULL OR b.isRemove = false",
           countQuery = "SELECT count(b) FROM Book b WHERE b.isRemove IS NULL OR b.isRemove = false")
    Page<Book> findAllActive(Pageable pageable);
}