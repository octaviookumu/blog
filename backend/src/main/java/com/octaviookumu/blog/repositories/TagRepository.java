package com.octaviookumu.blog.repositories;

import com.octaviookumu.blog.domain.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    /**
     * Will include all the posts needed
     * rather than relying on hibernate to go one by one in doing several queries<br>
     * LEFT JOIN: Fetches every single Tag, even if a tag has zero posts associated with it.<br>
     * FETCH: This is a JPA/Hibernate keyword.
     * It tells the framework to load the collection of posts immediately inside the same SQL query.
     * This completely prevents the N+1 select problem,
     * meaning Hibernate won't fire extra hidden queries later when you loop through tag.getPosts()
     */
    @Query("SELECT t from Tag t LEFT JOIN FETCH t.posts")
    List<Tag> findAllWithPostCount();
}
