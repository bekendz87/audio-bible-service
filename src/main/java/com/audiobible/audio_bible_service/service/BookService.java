package com.audiobible.audio_bible_service.service;

import com.audiobible.audio_bible_service.dto.response.BookResponse;
import com.audiobible.audio_bible_service.dto.response.ChapterResponse;
import com.audiobible.audio_bible_service.entity.Book;
import com.audiobible.audio_bible_service.entity.Chapter;
import com.audiobible.audio_bible_service.repository.BookRepository;
import com.audiobible.audio_bible_service.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;

    public List<BookResponse> getAllBooks(String testament) {
        List<Book> books = (testament != null && !testament.isEmpty())
                ? bookRepository.findByTestament(testament)
                : bookRepository.findAll();

        return books.stream().map(this::mapToBookResponse).toList();
    }

    public List<ChapterResponse> getChaptersByBookId(String bookId) {
        List<Chapter> chapters = chapterRepository.findByBookIdOrderByChapterNumAsc(bookId);
        return chapters.stream().map(this::mapToChapterResponse).toList();
    }

    private BookResponse mapToBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .subtitle(book.getSubtitle())
                .artworkUrl(book.getArtworkUrl())
                .testament(book.getTestament())
                .totalChapters(book.getTotalChapters())
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