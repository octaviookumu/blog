package com.octaviookumu.blog.service;

import com.octaviookumu.blog.domain.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> listCategories();

    Category createCategory(Category category
    );

    void deleteCategory(UUID id);

    Category getCategoryById(UUID id);
}
