package com.octaviookumu.blog.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    // Takes the email and password
    UserDetails authenticate(String email, String password);

    // Turns the details to a JWT
    String generateToken(UserDetails userDetails);

    // Turns the JWT into userDetails
    UserDetails validateToken(String token);
}
