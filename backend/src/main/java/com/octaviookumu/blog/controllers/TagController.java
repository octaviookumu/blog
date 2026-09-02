package com.octaviookumu.blog.controllers;

import com.octaviookumu.blog.domain.dtos.TagResponse;
import com.octaviookumu.blog.mappers.TagMapper;
import com.octaviookumu.blog.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<TagResponse> tagResponses = tagService.getTags().stream()
                .map(tagMapper::toTagResponse).toList();
        return ResponseEntity.ok(tagResponses);
    }
}
