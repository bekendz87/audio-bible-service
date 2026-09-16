package com.audiobible.audio_bible_service.controller;

import com.audiobible.audio_bible_service.dto.response.BookResponse;
import com.audiobible.audio_bible_service.dto.response.ChapterResponse;
import com.audiobible.audio_bible_service.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks(
            @RequestParam(required = false) String testament) {
        return ResponseEntity.ok(bookService.getAllBooks(testament));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<BookResponse>> getBooks(
            @RequestParam(required = false) String testament,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(bookService.getBooks(testament, page, size));
    }

    @GetMapping("/{bookId}/chapters")
    public ResponseEntity<List<ChapterResponse>> getChaptersByBook(@PathVariable int bookId) {
        return ResponseEntity.ok(bookService.getChaptersByBookId(bookId));
    }

    @GetMapping("/{bookId}/chapters/page")
    public ResponseEntity<Page<ChapterResponse>> getChaptersByBookPage(
            @PathVariable int bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(bookService.getChapters(bookId, page, size));
    }

    @GetMapping("/chapters/page")
    public ResponseEntity<Page<ChapterResponse>> getChaptersByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(bookService.getChaptersPage(page, size));
    }
}