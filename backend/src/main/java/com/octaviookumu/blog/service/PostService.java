package com.octaviookumu.blog.service;

import com.octaviookumu.blog.domain.CreatePostRequest;
import com.octaviookumu.blog.domain.UpdatePostRequest;
import com.octaviookumu.blog.domain.entity.Post;
import com.octaviookumu.blog.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {
    Post getPost(UUID id);

    Page<Post> getAllPosts(UUID categoryId, UUID tagId, Pageable pageable);

    Page<Post> getDraftPosts(User user, Pageable pageable);

    Post createPost(User user, CreatePostRequest createPostRequest);

    // Not looking to update the user to which the post belongs
    Post updatePost(UUID id, UpdatePostRequest updatePostRequest);

    void  deletePost(UUID id);
}
