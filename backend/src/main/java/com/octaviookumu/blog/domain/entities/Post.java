package com.octaviookumu.blog.domain.entities;

import com.octaviookumu.blog.domain.PostStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT") // TEXT allows varying lengths
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // because an enum is represented as a number
    private PostStatus status;

    @Column(nullable = false)
    private Integer readingTime;

    @ManyToOne(fetch = FetchType.LAZY) // no need for cascade since a post can't create/modify its author
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY) // no cascades needed as category and post have different life cycles
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany
    // joinColumns - Defines the foreign key column in the post_tags table that points back to the owning entity (Post).
    // It maps the id of the Post into a column named post_id
    // inverseJoinColumns - Defines the foreign key column in the post_tags table that points to the target entity (Tag).
    // It maps the id of the Tag into a column named tag_id
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>(); // set prevents duplicates and improves performance, not concerned with order

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) && Objects.equals(title, post.title) && Objects.equals(content, post.content) && status == post.status && Objects.equals(readingTime, post.readingTime) && Objects.equals(createdAt, post.createdAt) && Objects.equals(updatedAt, post.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, status, readingTime, createdAt, updatedAt);
    }

    @PrePersist // called when entity is created
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate // called when entity is updated
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
