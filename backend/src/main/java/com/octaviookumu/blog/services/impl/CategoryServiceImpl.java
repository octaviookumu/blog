package com.octaviookumu.blog.services.impl;

import com.octaviookumu.blog.domain.entities.Category;
import com.octaviookumu.blog.repositories.CategoryRepository;
import com.octaviookumu.blog.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> listCategories() {
        return categoryRepository.findAllWithPostCount();
    }
}
