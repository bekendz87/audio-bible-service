package com.audiobible.audio_bible_service.repository;

import com.audiobible.audio_bible_service.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

   
    boolean existsByNameIgnoreCaseAndIsRemoveFalseOrIsRemoveIsNull(String name);

    List<Category> findByIsRemoveFalseOrIsRemoveIsNull(Sort sort);
}