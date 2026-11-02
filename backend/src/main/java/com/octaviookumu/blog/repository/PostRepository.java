package com.octaviookumu.blog.repository;

import com.octaviookumu.blog.domain.PostStatus;
import com.octaviookumu.blog.domain.entity.Category;
import com.octaviookumu.blog.domain.entity.Post;
import com.octaviookumu.blog.domain.entity.Tag;
import com.octaviookumu.blog.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * @param pageable pageable
     * @return a list of posts
     */
    Page<Post> findAllByStatusAndCategoryAndTagsContaining(PostStatus status, Category category, Tag tag, Pageable pageable);

    Page<Post> findAllByStatusAndCategory(PostStatus status, Category category, Pageable pageable);

    Page<Post> findAllByStatusAndTagsContaining(PostStatus status, Tag tag, Pageable pageable);

    Page<Post> findAllByStatus(PostStatus status, Pageable pageable);

    Page<Post> findAllByAuthorAndStatus(User user, PostStatus status, Pageable pageable);
}
