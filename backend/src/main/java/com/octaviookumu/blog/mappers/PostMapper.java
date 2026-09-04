package com.octaviookumu.blog.mappers;

import com.octaviookumu.blog.domain.CreatePostRequest;
import com.octaviookumu.blog.domain.dtos.CreatePostRequestDto;
import com.octaviookumu.blog.domain.dtos.PostDto;
import com.octaviookumu.blog.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    // Because inside our post we have our user/author, category and tags, we add some mapping annotations
    // Essentially means: copy post.author (source) into postDto.author (target)
    @Mapping(target = "author", source = "author") //
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", source = "tags")
    PostDto toDto(Post post);

    CreatePostRequest toCreatePostRequest(CreatePostRequestDto createPostRequestDto);
}
