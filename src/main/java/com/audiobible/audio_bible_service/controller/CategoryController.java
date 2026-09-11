package com.audiobible.audio_bible_service.controller;

import com.audiobible.audio_bible_service.dto.response.CategoryResponse;
import com.audiobible.audio_bible_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {
    private final CategoryRepository repository;

    @GetMapping
    public List<CategoryResponse> getCategories() {
        return repository.findAll().stream().map(c -> CategoryResponse.builder()
                .id(c.getId()).name(c.getName()).description(c.getDescription()).build()).toList();
    }
}
