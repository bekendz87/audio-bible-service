package com.audiobible.audio_bible_service.service;

import com.audiobible.audio_bible_service.dto.request.*;
import com.audiobible.audio_bible_service.dto.response.*;
import com.audiobible.audio_bible_service.entity.*;
import com.audiobible.audio_bible_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminContentService {
    private final BookRepository books;
    private final ChapterRepository chapters;
    private final CategoryRepository categories;

    @CacheEvict(value = "books", allEntries = true)
    public CategoryResponse saveCategory(CategoryRequest r, int id) {
        Category c = id != 0 ? new Category()
                : categories.findById(id).orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        c.setName(r.getName());
        c.setDescription(r.getDescription());
        return map(categories.save(c));
    }

    public List<CategoryResponse> categories() {
        return categories.findAll(Sort.by("name")).stream().map(this::map).toList();
    }

    @CacheEvict(value = "books", allEntries = true)
    public void deleteCategory(int id) {
        categories.deleteById(id);
    }

    @CacheEvict(value = "books", allEntries = true)
    public BookResponse saveBook(BookRequest r, int id) {
        Book b = id != 0 ? new Book()
                : books.findById(id).orElseThrow(() -> new RuntimeException("Sách không tồn tại"));

        b.setTitle(r.getTitle());
        b.setTestament(r.getTestament());
        b.setTotalChapters(r.getTotalChapters());
        b.setSubtitle(r.getSubtitle());
        b.setArtworkUrl(r.getArtworkUrl());
        b.setCategory(r.getCategoryId() != 0 ? categories.findById(r.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại")) : null);
        return map(books.save(b));
    }

    @CacheEvict(value = { "books", "chapters" }, allEntries = true)
    public void deleteBook(int id) {
        books.deleteById(id);
    }

    @CacheEvict(value = "chapters", allEntries = true)
    public ChapterResponse saveChapter(ChapterRequest r, int id) {
        Chapter c = id != 0 ? new Chapter()
                : chapters.findById(id).orElseThrow(() -> new RuntimeException("Chương không tồn tại"));

        c.setBook(books.findById(r.getBookId()).orElseThrow(() -> new RuntimeException("Sách không tồn tại")));
        c.setChapterNum(r.getChapterNum());
        c.setReader(r.getReader());
        c.setAudioUrl(r.getAudioUrl());
        c.setDuration(r.getDuration());
        Chapter saved = chapters.save(c);
        return ChapterResponse.builder().id(saved.getId()).bookId(saved.getBook().getId())
                .bookName(saved.getBook().getTitle()).chapterNum(saved.getChapterNum()).reader(saved.getReader())
                .audioUrl(saved.getAudioUrl()).duration(saved.getDuration()).build();
    }

    @CacheEvict(value = "chapters", allEntries = true)
    public void deleteChapter(int id) {
        chapters.deleteById(id);
    }

    private CategoryResponse map(Category c) {
        return CategoryResponse.builder().id(c.getId()).name(c.getName()).description(c.getDescription()).build();
    }

    private BookResponse map(Book b) {
        return BookResponse.builder().id(b.getId()).title(b.getTitle()).subtitle(b.getSubtitle())
                .artworkUrl(b.getArtworkUrl()).testament(b.getTestament()).totalChapters(b.getTotalChapters())
                .categoryId(b.getCategory() == null ? null : b.getCategory().getId())
                .categoryName(b.getCategory() == null ? null : b.getCategory().getName()).build();
    }
}
