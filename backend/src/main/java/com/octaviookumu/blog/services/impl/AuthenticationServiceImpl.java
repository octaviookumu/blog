package com.octaviookumu.blog.services.impl;

import com.octaviookumu.blog.services.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    // btw these don't come from the project but the packages above
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    // will be loaded from configurations... we could do it using config properties, but this is a simple way also
    private String secretKey;

    // btw Long is a nullable Java Object (Wrapper class) while long is a non-nullable primitive type
    private final Long jwtExpiryMs = 86400000L; // 24 hrs

    @Override
    public UserDetails authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return userDetailsService.loadUserByUsername(email);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        // tokens contain claims, so that's the first thing we'll need to create
        Map<String, Object> claims = new HashMap<>();

        // create a JWT
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiryMs))
                .signWith(getSigningKey(), Jwts.SIG.HS256) // sign the jwt using our secret
                .compact(); // will give a string
    }

    /**
     * Now that we have this method we can use it in a custom filter
     */
    @Override
    public UserDetails validateToken(String token) {
        String username = extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    /**
     * Extracts the username from the token
     */
    private String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey()) // an exception will be thrown if say, the sign in key doesn't match
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject(); // the username
    }


    /**
     * Signs the jwt using our secret
     */
    private SecretKey getSigningKey() {          // <-- Change 'Key' to 'SecretKey' here
        byte[] keyBytes = this.secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
