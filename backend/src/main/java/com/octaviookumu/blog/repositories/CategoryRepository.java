package com.octaviookumu.blog.repositories;

import com.octaviookumu.blog.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * "Give me every category, and load its posts at the same time."<br>
     * This HQL query sidesteps the N+1 problem which happens when your code loads a list of parent records,
     * then triggers one extra query per parent to load related records.<br>
     * SELECT c -> Return Category objects (c). means each category should in the returned list.<br>
     * FROM Category c -> Start with all Category entities. c is a short nickname used elsewhere in the query.<br>
     * LEFT JOIN FETCH c.posts
     * For each category, join its posts relationship and load those Post objects too.
     * - LEFT JOIN: keep a category even if it has no posts.
     * - FETCH: put the matching posts into category.getPosts() immediately.
     *
     * @return
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.posts")
    //
    List<Category> findAllWithPostCount();

    /**
     * Spring DataJPA can work out what exactly to do with this
     *
     * @param name is the provided category
     * @return whether true or false
     */
    boolean existsByNameIgnoreCase(String name);
}
