package com.octaviookumu.blog.service;

import com.octaviookumu.blog.domain.entity.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);

    User createUser(String name, String email, String rawPassword);
}
