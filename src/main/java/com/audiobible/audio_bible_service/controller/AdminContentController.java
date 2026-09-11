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
        return service.saveCategory(request, null);
    }

    @PutMapping("/categories/{id}")
    public CategoryResponse updateCategory(@PathVariable String id, @Valid @RequestBody CategoryRequest request) {
        return service.saveCategory(request, id);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/books")
    public BookResponse createBook(@Valid @RequestBody BookRequest request) {
        return service.saveBook(request, null);
    }

    @PutMapping("/books/{id}")
    public BookResponse updateBook(@PathVariable String id, @Valid @RequestBody BookRequest request) {
        return service.saveBook(request, id);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        service.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/chapters")
    public ChapterResponse createChapter(@Valid @RequestBody ChapterRequest request) {
        return service.saveChapter(request, null);
    }

    @PutMapping("/chapters/{id}")
    public ChapterResponse updateChapter(@PathVariable String id, @Valid @RequestBody ChapterRequest request) {
        return service.saveChapter(request, id);
    }

    @DeleteMapping("/chapters/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable String id) {
        service.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }
}
