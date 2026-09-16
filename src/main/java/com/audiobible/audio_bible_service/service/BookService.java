package com.audiobible.audio_bible_service.service;

import com.audiobible.audio_bible_service.dto.response.BookResponse;
import com.audiobible.audio_bible_service.dto.response.ChapterResponse;
import com.audiobible.audio_bible_service.entity.Book;
import com.audiobible.audio_bible_service.entity.Chapter;
import com.audiobible.audio_bible_service.repository.BookRepository;
import com.audiobible.audio_bible_service.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "books", key = "#testament != null ? #testament : 'ALL'")
    public List<BookResponse> getAllBooks(String testament) {
        List<Book> books = (testament != null && !testament.isBlank())
                ? bookRepository.findByTestamentIgnoreCaseAndIsRemoveFalseOrIsRemoveIsNull(testament)
                : bookRepository.findByIsRemoveFalseOrIsRemoveIsNull();

        return books.stream().map(this::mapToBookResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(String testament, int page, int size) {
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by("title").ascending());

        Page<Book> books = (testament != null && !testament.isBlank())
                ? bookRepository.findByTestamentIgnoreCaseAndIsRemoveFalseOrIsRemoveIsNull(testament, pageable)
                : bookRepository.findAllActive(pageable);

        return books.map(this::mapToBookResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "chapters", key = "#bookId")
    public List<ChapterResponse> getChaptersByBookId(int bookId) {
        List<Chapter> chapters = chapterRepository.findByBookIdAndIsRemoveFalseOrIsRemoveIsNull(bookId);
        return chapters.stream().map(this::mapToChapterResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<ChapterResponse> getChapters(int bookId, int page, int size) {
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by("id").ascending());
        return chapterRepository.findByBookIdAndIsRemoveFalseOrIsRemoveIsNull(bookId, pageable)
                .map(this::mapToChapterResponse);
    }

    @Transactional(readOnly = true)
    public Page<ChapterResponse> getChaptersPage(int page, int size) {
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by("id").ascending());

        Optional<Chapter> firstChapter = chapterRepository.findFirstByIsRemoveFalseOrIsRemoveIsNullOrderByIdAsc();

        if (firstChapter.isEmpty()) {
            return Page.empty(pageable);
        }

        Integer bookId = firstChapter.get().getBook().getId();

        return chapterRepository.findByBookIdAndIsRemoveFalseOrIsRemoveIsNull(bookId, pageable)
                .map(this::mapToChapterResponse);
    }

    private BookResponse mapToBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .testament(book.getTestament())
                .totalChapters(book.getTotalChapters())
                .subtitle(book.getSubtitle())
                .artworkUrl(book.getArtworkUrl())
                .categoryId(book.getCategory() != null ? book.getCategory().getId() : null)
                .categoryName(book.getCategory() != null ? book.getCategory().getName() : null)
                .createdAt(book.getCreatedAt())
                .build();
    }

    private ChapterResponse mapToChapterResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .bookId(chapter.getBook().getId())
                .bookName(chapter.getBook().getTitle())
                .reader(chapter.getReader())
                .title(chapter.getTitle())
                .remove(chapter.getIsRemove())
                .audioUrl(chapter.getAudioUrl())
                .duration(chapter.getDuration())
                .build();
    }
}