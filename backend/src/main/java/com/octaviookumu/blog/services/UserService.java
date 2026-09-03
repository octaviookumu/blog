package com.octaviookumu.blog.services;

import com.octaviookumu.blog.domain.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
}
