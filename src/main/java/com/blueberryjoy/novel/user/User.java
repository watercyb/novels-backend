package com.blueberryjoy.novel.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {@Index(name = "idx_email", columnList = "email")})
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "avatar_url", length = 2048)
    private String avatarUrl;

    @Column(name = "novel_count", length = 2048)
    private int novelCount;

    @Column(name = "chapter_count", length = 2048)
    private int chapterCount;

    @Column(name = "word_count", length = 2048)
    private int wordCount;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void updateNovelCount(int n) {
        novelCount += n;
    }

    public void updateChapterCount(int n) {
        chapterCount += n;
    }

    public void updateWordCount(int n) {
        wordCount += n;
    }

    public enum Role {
        USER, AUTHOR, ADMIN, SUPERADMIN;

        public static User.Role getRole(String value) {
            if (value == null)
                return null;
            try {
                return User.Role.valueOf(value.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
