package com.octaviookumu.blog.services.impl;

import com.octaviookumu.blog.domain.PostStatus;
import com.octaviookumu.blog.domain.entities.Category;
import com.octaviookumu.blog.domain.entities.Post;
import com.octaviookumu.blog.domain.entities.Tag;
import com.octaviookumu.blog.domain.entities.User;
import com.octaviookumu.blog.repositories.PostRepository;
import com.octaviookumu.blog.services.CategoryService;
import com.octaviookumu.blog.services.PostService;
import com.octaviookumu.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

    @Override
    @Transactional(readOnly = true) // we are making multiple db calls we are not writing anything.
    // This is available when you import from the springframework one rather than the jakarta one
    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
        if (categoryId != null && tagId != null) {
            // published posts that match both the category and tag id
            Category category = categoryService.getCategoryById(categoryId);
            Tag tag = tagService.getTagById(tagId);
            return postRepository
                    .findAllByStatusAndCategoryAndTagsContaining(
                            PostStatus.PUBLISHED,
                            category,
                            tag
                    );
        }
        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            return postRepository.findAllByStatusAndCategory(
                    PostStatus.PUBLISHED,
                    category
            );
        }

        if (tagId != null) {
            Tag tag = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndTagsContaining(
                    PostStatus.PUBLISHED,
                    tag
            );
        }

        return postRepository.findAllByStatus(PostStatus.PUBLISHED);
    }

    @Override
    public List<Post> getDraftPosts(User user) {
        return postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT);
    }
}
