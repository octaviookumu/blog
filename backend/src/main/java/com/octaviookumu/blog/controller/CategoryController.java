package com.octaviookumu.blog.controller;

import com.octaviookumu.blog.domain.dto.CategoryDto;
import com.octaviookumu.blog.domain.dto.CreateCategoryRequestDto;
import com.octaviookumu.blog.domain.entity.Category;
import com.octaviookumu.blog.mapper.CategoryMapper;
import com.octaviookumu.blog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> listCategories() {
        List<CategoryDto> categories = categoryService.listCategories()
                .stream().map(categoryMapper::toDto)
                .toList();

        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CreateCategoryRequestDto createCategoryRequestDto) {
        Category categoryToCreate = categoryMapper.toEntity(createCategoryRequestDto);
        Category savedCategory = categoryService.createCategory(categoryToCreate);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryMapper.toDto(savedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
