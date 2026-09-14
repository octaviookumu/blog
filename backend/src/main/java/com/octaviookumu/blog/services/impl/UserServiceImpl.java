package com.octaviookumu.blog.services.impl;

import com.octaviookumu.blog.domain.entities.User;
import com.octaviookumu.blog.repositories.UserRepository;
import com.octaviookumu.blog.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    @Override
    public User createUser(String name, String email, String rawPassword) {
        userRepository.findByEmail(email).ifPresent(existing -> {
            throw new IllegalStateException("User with email " + email + " already exists");
        });

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .build();

        return userRepository.save(user);
    }
}
