package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.tag.Tag;
import com.blueberryjoy.novel.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "novels", indexes = {@Index(name = "idx_author_created", columnList = "user_id, created_at DESC")})
@Getter
@Setter
public class Novel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "novel_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(name = "cover_url", length = 2048)
    private String coverUrl;

    @ManyToMany
    @JoinTable(
            name = "novel_tags",
            joinColumns = @JoinColumn(name = "novel_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    private int likes;

    private int chapters;

    private int words;

    @Column(name = "last_chapter_title", nullable = false)
    private String lastChapterTitle;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getNovels().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getNovels().remove(this);
    }
}
