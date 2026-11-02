package com.octaviookumu.blog.controller;

import com.octaviookumu.blog.domain.CreatePostRequest;
import com.octaviookumu.blog.domain.UpdatePostRequest;
import com.octaviookumu.blog.domain.dto.CreatePostRequestDto;
import com.octaviookumu.blog.domain.dto.PostDto;
import com.octaviookumu.blog.domain.dto.UpdatePostRequestDto;
import com.octaviookumu.blog.domain.entity.Post;
import com.octaviookumu.blog.domain.entity.User;
import com.octaviookumu.blog.mapper.PostMapper;
import com.octaviookumu.blog.service.PostService;
import com.octaviookumu.blog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;

    // the params will allow us to filter posts based on the category and tag or both
    @GetMapping
    public ResponseEntity<Page<PostDto>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId,
            Pageable pageable
    ) {
        Page<Post> posts = postService.getAllPosts(categoryId, tagId, pageable);
        Page<PostDto> postDtos = posts.map(postMapper::toDto);
        return ResponseEntity.ok(postDtos);
    }

    // Gets the request attribute being set in the JwtFilter
    @GetMapping("/drafts")
    public ResponseEntity<Page<PostDto>> getDrafts(
            @RequestAttribute UUID userId,
            Pageable pageable
    ) {
        // filter drafts based on logged in user
        User loggedInUser = userService.getUserById(userId);
        Page<PostDto> draftPostDtos = postService.getDraftPosts(loggedInUser, pageable)
                .map(postMapper::toDto);
        return ResponseEntity.ok(draftPostDtos);
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody CreatePostRequestDto createPostRequestDto,
            @RequestAttribute UUID userId
    ) {
        System.out.println("Request attribute userId: " + userId);
        User loggedInUser = userService.getUserById(userId);
        // we shouldn't pass a dto to our service layer
        // a representation of the create post information
        CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDto);
        Post createdPost = postService.createPost(loggedInUser, createPostRequest);
        PostDto createdPostDto = postMapper.toDto(createdPost);
        return new ResponseEntity<>(createdPostDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequestDto updatePostRequestDto
    ) {
        // as we can't pass our dto to the service layer, we need to create a updatePostRequest
        UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(updatePostRequestDto);
        Post updatedPost = postService.updatePost(id, updatePostRequest);
        PostDto updatedPostDto = postMapper.toDto(updatedPost);
        return ResponseEntity.ok(updatedPostDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable UUID id) {
        Post post = postService.getPost(id);
        PostDto postDto = postMapper.toDto(post);
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

}
