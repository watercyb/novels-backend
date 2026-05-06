package com.blueberryjoy.novel.comment;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_chapter_create", columnList = "chapter_id, created_at DESC"),
                @Index(name = "idx_parent_create", columnList = "parent_id, created_at DESC")
        }
)
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "comment_count")
    private int commentCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void updateCommentCount(int n) {
        this.commentCount += n;
    }
}
