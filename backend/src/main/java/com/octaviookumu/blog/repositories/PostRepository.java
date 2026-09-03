package com.octaviookumu.blog.repositories;

import com.octaviookumu.blog.domain.PostStatus;
import com.octaviookumu.blog.domain.entities.Category;
import com.octaviookumu.blog.domain.entities.Post;
import com.octaviookumu.blog.domain.entities.Tag;
import com.octaviookumu.blog.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    /**
     * Details for this naming can be found in  <a href="https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html">
     * JPA Query Methods docs</a>
     * <p>{@code TagsContaining} is used because {@code Set<Tag> tags = new HashSet<>()}
     * is a collection of tags {@code (@ManyToMany)}
     * </p>
     *
     * @param status   post status
     * @param category category
     * @param tag      tag
     * @return a list of posts
     */
    List<Post> findAllByStatusAndCategoryAndTagsContaining(PostStatus status, Category category, Tag tag);

    List<Post> findAllByStatusAndCategory(PostStatus status, Category category);

    List<Post> findAllByStatusAndTagsContaining(PostStatus status, Tag tag);

    List<Post> findAllByStatus(PostStatus status);

    List<Post> findAllByAuthorAndStatus(User user, PostStatus status);
}
