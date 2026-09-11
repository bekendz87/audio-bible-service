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
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;

    @Cacheable(value = "books", key = "#testament != null ? #testament : 'ALL'")
    public List<BookResponse> getAllBooks(String testament) {
        List<Book> books = (testament != null && !testament.isEmpty())
                ? bookRepository.findByTestament(testament)
                : bookRepository.findAll();

        return books.stream().map(this::mapToBookResponse).toList();
    }

        public Page<BookResponse> getBooks(String testament, int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
            Sort.by("title").ascending());
        Page<Book> books = (testament != null && !testament.isBlank())
            ? bookRepository.findByTestamentIgnoreCase(testament, pageable)
            : bookRepository.findAll(pageable);
        return books.map(this::mapToBookResponse);
        }
    @Cacheable(value = "chapters", key = "#bookId")
    public List<ChapterResponse> getChaptersByBookId(String bookId) {
        List<Chapter> chapters = chapterRepository.findByBookIdOrderByChapterNumAsc(bookId);
        return chapters.stream().map(this::mapToChapterResponse).toList();
    }

    public Page<ChapterResponse> getChapters(String bookId, int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by("chapterNum").ascending());
        return chapterRepository.findByBookId(bookId, pageable).map(this::mapToChapterResponse);
    }

    private BookResponse mapToBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .subtitle(book.getSubtitle())
                .artworkUrl(book.getArtworkUrl())
                .testament(book.getTestament())
                .totalChapters(book.getTotalChapters())
                .categoryId(book.getCategory() == null ? null : book.getCategory().getId())
                .categoryName(book.getCategory() == null ? null : book.getCategory().getName())
                .build();
    }

    private ChapterResponse mapToChapterResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .bookId(chapter.getBook().getId())
                .bookName(chapter.getBook().getTitle())
                .chapterNum(chapter.getChapterNum())
                .reader(chapter.getReader())
                .audioUrl(chapter.getAudioUrl())
                .duration(chapter.getDuration())
                .build();
    }
}