package com.octaviookumu.blog.services;

import com.octaviookumu.blog.domain.entities.Post;

import java.util.List;
import java.util.UUID;

public interface PostService {
    List<Post> getAllPosts(UUID categoryId, UUID tagId); // TODO: look into paginating
}
