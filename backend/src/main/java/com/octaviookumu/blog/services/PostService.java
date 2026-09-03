package com.octaviookumu.blog.services;

import com.octaviookumu.blog.domain.entities.Post;
import com.octaviookumu.blog.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {
    List<Post> getAllPosts(UUID categoryId, UUID tagId); // TODO: look into paginating

    List<Post> getDraftPosts(User user);
}
