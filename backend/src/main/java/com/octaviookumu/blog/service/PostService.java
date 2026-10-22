package com.octaviookumu.blog.service;

import com.octaviookumu.blog.domain.CreatePostRequest;
import com.octaviookumu.blog.domain.UpdatePostRequest;
import com.octaviookumu.blog.domain.entity.Post;
import com.octaviookumu.blog.domain.entity.User;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Post getPost(UUID id);

    List<Post> getAllPosts(UUID categoryId, UUID tagId); // TODO: look into paginating

    List<Post> getDraftPosts(User user);

    Post createPost(User user, CreatePostRequest createPostRequest);

    // Not looking to update the user to which the post belongs
    Post updatePost(UUID id, UpdatePostRequest updatePostRequest);

    void  deletePost(UUID id);
}
