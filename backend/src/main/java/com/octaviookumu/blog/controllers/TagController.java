package com.octaviookumu.blog.controllers;

import com.octaviookumu.blog.domain.dtos.CreateTagsRequest;
import com.octaviookumu.blog.domain.dtos.TagResponse;
import com.octaviookumu.blog.domain.entities.Tag;
import com.octaviookumu.blog.mappers.TagMapper;
import com.octaviookumu.blog.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<List<TagResponse>> createTag(@Valid @RequestBody CreateTagsRequest createTagsRequest) {
        List<Tag> savedTags = tagService.createTags(createTagsRequest.getNames());
        List<TagResponse> createdTagResponses = savedTags.stream()
                .map(tagMapper::toTagResponse).toList();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTagResponses);
    }

}
