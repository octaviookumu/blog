package com.octaviookumu.blog.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDto {
    // this information is baked into the token itself, but it's always useful to have it available easily
    private String token;
    private long expiresIn;
}
