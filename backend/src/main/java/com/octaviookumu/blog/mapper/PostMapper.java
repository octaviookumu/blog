package com.octaviookumu.blog.mapper;

import com.octaviookumu.blog.domain.CreatePostRequest;
import com.octaviookumu.blog.domain.UpdatePostRequest;
import com.octaviookumu.blog.domain.dto.CreatePostRequestDto;
import com.octaviookumu.blog.domain.dto.PostDto;
import com.octaviookumu.blog.domain.dto.UpdatePostRequestDto;
import com.octaviookumu.blog.domain.entity.Post;
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
    @Mapping(target = "status", source = "status")
    PostDto toDto(Post post);

    CreatePostRequest toCreatePostRequest(CreatePostRequestDto dto);

    UpdatePostRequest toUpdatePostRequest(UpdatePostRequestDto dto);
}
