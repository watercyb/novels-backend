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
@Table(
        name = "novels",
        indexes = {
                @Index(name = "idx_like", columnList = "like_count DESC"),
                @Index(name = "idx_chapter", columnList = "chapter_count DESC"),
                @Index(name = "idx_word", columnList = "word_count DESC"),
                @Index(name = "idx_last", columnList = "last_updated_at DESC"),
                @Index(name = "idx_author_created", columnList = "user_id, created_at DESC"),
                @Index(name = "idx_user_title", columnList = "user_id, title"),
                @Index(name = "idx_user_like", columnList = "user_id, like_count DESC"),
                @Index(name = "idx_user_chapter", columnList = "user_id, chapter_count DESC"),
                @Index(name = "idx_user_word", columnList = "user_id, word_count DESC"),
                @Index(name = "idx_user_last", columnList = "user_id, last_updated_at DESC"),
                @Index(name = "idx_type_like", columnList = "type, like_count DESC"),
                @Index(name = "idx_type_chapter", columnList = "type, chapter_count DESC"),
                @Index(name = "idx_type_word", columnList = "type, word_count DESC"),
                @Index(name = "idx_type_last", columnList = "type, last_updated_at DESC"),
                @Index(name = "idx_type_author_created", columnList = "type, user_id, created_at DESC"),
                @Index(name = "idx_type_user_title", columnList = "type, user_id, title"),
                @Index(name = "idx_type_user_like", columnList = "type, user_id, like_count DESC"),
                @Index(name = "idx_type_user_chapter", columnList = "type, user_id, chapter_count DESC"),
                @Index(name = "idx_type_user_word", columnList = "type, user_id, word_count DESC"),
                @Index(name = "idx_type_user_last", columnList = "type, user_id, last_updated_at DESC")
        }
)
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
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            indexes = {
                    @Index(name = "idx_novel_tags_tag_id", columnList = "tag_id"),
                    @Index(name = "idx_novel_tags_novel_id", columnList = "novel_id"),
                    @Index(name = "idx_novel_tags_tag_novel", columnList = "tag_id, novel_id")
            }
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "chapter_count", nullable = false)
    private int chapterCount;

    @Column(name = "word_count", nullable = false)
    private int wordCount;

    @Column(name = "last_chapter_title", nullable = false)
    private String lastChapterTitle;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    private Novel.Type type;

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

    public enum Type {
        ORIGINAL, TRANSLATION;

        public static Type getType(String value) {
            if (value == null)
                return null;
            try {
                return Type.valueOf(value.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
