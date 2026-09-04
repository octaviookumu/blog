package com.octaviookumu.blog.controllers;

import com.octaviookumu.blog.domain.CreatePostRequest;
import com.octaviookumu.blog.domain.dtos.CreatePostRequestDto;
import com.octaviookumu.blog.domain.dtos.PostDto;
import com.octaviookumu.blog.domain.entities.Post;
import com.octaviookumu.blog.domain.entities.User;
import com.octaviookumu.blog.mappers.PostMapper;
import com.octaviookumu.blog.services.PostService;
import com.octaviookumu.blog.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId
    ) {
        List<Post> posts = postService.getAllPosts(categoryId, tagId);
        List<PostDto> postDtos = posts.stream().map(postMapper::toDto).toList();
        return ResponseEntity.ok(postDtos);
    }

    // Gets the request attribute being set in the JwtFilter
    @GetMapping("/drafts")
    public ResponseEntity<List<PostDto>> getDrafts(@RequestAttribute UUID userId) {
        // filter drafts based on logged in user
        User loggedInUser = userService.getUserById(userId);
        List<PostDto> draftPostDtos = postService.getDraftPosts(loggedInUser).stream()
                .map(postMapper::toDto)
                .toList();
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

}
