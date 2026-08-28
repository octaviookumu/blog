package com.octaviookumu.blog.mappers;

import com.octaviookumu.blog.domain.PostStatus;
import com.octaviookumu.blog.domain.dtos.CategoryDto;
import com.octaviookumu.blog.domain.dtos.CreateCategoryRequest;
import com.octaviookumu.blog.domain.entities.Category;
import com.octaviookumu.blog.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    /**
     * This mapping tells MapStruct how to fill CategoryDto.postCount <br>
     * Take category.getPosts(), pass it to calculatePostCount(...),
     * and store the returned number in CategoryDto.postCount
     *
     * @param category is the category
     * @return the count
     */
    @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequest createCategoryRequest);

    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts) {
        if (posts == null) {
            return 0;
        }
        return posts.stream()
                .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                .count();
    }
}
