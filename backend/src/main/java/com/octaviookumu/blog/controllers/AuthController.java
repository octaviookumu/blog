package com.octaviookumu.blog.controllers;

import com.octaviookumu.blog.domain.dtos.AuthResponseDto;
import com.octaviookumu.blog.domain.dtos.LoginRequestDto;
import com.octaviookumu.blog.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        UserDetails userDetails = authenticationService
                .authenticate(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                );
        String tokenValue = authenticationService.generateToken(userDetails);
        AuthResponseDto authResponseDto = AuthResponseDto.builder()
                .token(tokenValue)
                .expiresIn(3600) // 1hr
                .build();
        return ResponseEntity.ok(authResponseDto);

    }

}
