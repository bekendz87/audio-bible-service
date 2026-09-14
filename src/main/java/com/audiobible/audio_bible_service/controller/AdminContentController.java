package com.audiobible.audio_bible_service.controller;

import com.audiobible.audio_bible_service.dto.request.*;
import com.audiobible.audio_bible_service.dto.response.*;
import com.audiobible.audio_bible_service.service.AdminContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.audiobible.audio_bible_service.config.AdminApiController;
import java.util.List;

@RestController
@AdminApiController
@RequestMapping("/")
@RequiredArgsConstructor
public class AdminContentController {
    private final AdminContentService service;

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return service.categories();
    }

    @PostMapping("/categories")
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return service.saveCategory(request, 0);
    }

    @PutMapping("/categories/{id}")
    public CategoryResponse updateCategory(@PathVariable int id, @Valid @RequestBody CategoryRequest request) {
        return service.saveCategory(request, id);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/books")
    public BookResponse createBook(@Valid @RequestBody BookRequest request) {
        return service.saveBook(request, 0);
    }

    @PutMapping("/books/{id}")
    public BookResponse updateBook(@PathVariable int id, @Valid @RequestBody BookRequest request) {
        return service.saveBook(request, id);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        service.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/chapters")
    public ChapterResponse createChapter(@Valid @RequestBody ChapterRequest request) {
        return service.saveChapter(request, 0);
    }

    @PutMapping("/chapters/{id}")
    public ChapterResponse updateChapter(@PathVariable int id, @Valid @RequestBody ChapterRequest request) {
        return service.saveChapter(request, id);
    }

    @DeleteMapping("/chapters/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable int id) {
        service.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }
}
