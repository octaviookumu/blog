package com.octaviookumu.blog.controllers;

import com.octaviookumu.blog.domain.dtos.CreateTagsRequest;
import com.octaviookumu.blog.domain.dtos.TagDto;
import com.octaviookumu.blog.domain.entities.Tag;
import com.octaviookumu.blog.mappers.TagMapper;
import com.octaviookumu.blog.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags() {
        List<TagDto> tagDto = tagService.getTags().stream()
                .map(tagMapper::toDto).toList();
        return ResponseEntity.ok(tagDto);
    }

    @PostMapping
    public ResponseEntity<List<TagDto>> createTag(@Valid @RequestBody CreateTagsRequest createTagsRequest) {
        List<Tag> savedTags = tagService.createTags(createTagsRequest.getNames());
        List<TagDto> createdTagDto = savedTags.stream()
                .map(tagMapper::toDto).toList();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTagDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

}
