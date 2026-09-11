package com.audiobible.audio_bible_service.service;

import com.audiobible.audio_bible_service.dto.request.*;
import com.audiobible.audio_bible_service.dto.response.*;
import com.audiobible.audio_bible_service.entity.*;
import com.audiobible.audio_bible_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminContentService {
    private final BookRepository books;
    private final ChapterRepository chapters;
    private final CategoryRepository categories;

    public CategoryResponse saveCategory(CategoryRequest r, String id) {
        Category c = id == null ? new Category() : categories.findById(id).orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        c.setName(r.getName()); c.setDescription(r.getDescription());
        return map(categories.save(c));
    }
    public List<CategoryResponse> categories() { return categories.findAll(Sort.by("name")).stream().map(this::map).toList(); }
    public void deleteCategory(String id) { categories.deleteById(id); }

    public BookResponse saveBook(BookRequest r, String id) {
        Book b = id == null ? new Book() : books.findById(id).orElseThrow(() -> new RuntimeException("Sách không tồn tại"));
        if (id == null) b.setId(r.getId() == null || r.getId().isBlank() ? java.util.UUID.randomUUID().toString() : r.getId());
        b.setTitle(r.getTitle()); b.setTestament(r.getTestament()); b.setTotalChapters(r.getTotalChapters());
        b.setSubtitle(r.getSubtitle()); b.setArtworkUrl(r.getArtworkUrl());
        b.setCategory(r.getCategoryId() == null ? null : categories.findById(r.getCategoryId()).orElseThrow(() -> new RuntimeException("Danh mục không tồn tại")));
        return map(books.save(b));
    }
    public void deleteBook(String id) { books.deleteById(id); }

    public ChapterResponse saveChapter(ChapterRequest r, String id) {
        Chapter c = id == null ? new Chapter() : chapters.findById(id).orElseThrow(() -> new RuntimeException("Chương không tồn tại"));
        if (id == null) c.setId(r.getId() == null || r.getId().isBlank() ? java.util.UUID.randomUUID().toString() : r.getId());
        c.setBook(books.findById(r.getBookId()).orElseThrow(() -> new RuntimeException("Sách không tồn tại")));
        c.setChapterNum(r.getChapterNum()); c.setReader(r.getReader()); c.setAudioUrl(r.getAudioUrl()); c.setDuration(r.getDuration());
        Chapter saved = chapters.save(c);
        return ChapterResponse.builder().id(saved.getId()).bookId(saved.getBook().getId()).bookName(saved.getBook().getTitle()).chapterNum(saved.getChapterNum()).reader(saved.getReader()).audioUrl(saved.getAudioUrl()).duration(saved.getDuration()).build();
    }
    public void deleteChapter(String id) { chapters.deleteById(id); }

    private CategoryResponse map(Category c) { return CategoryResponse.builder().id(c.getId()).name(c.getName()).description(c.getDescription()).build(); }
    private BookResponse map(Book b) { return BookResponse.builder().id(b.getId()).title(b.getTitle()).subtitle(b.getSubtitle()).artworkUrl(b.getArtworkUrl()).testament(b.getTestament()).totalChapters(b.getTotalChapters()).categoryId(b.getCategory() == null ? null : b.getCategory().getId()).categoryName(b.getCategory() == null ? null : b.getCategory().getName()).build(); }
}
