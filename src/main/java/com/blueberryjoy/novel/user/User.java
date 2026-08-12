package com.blueberryjoy.novel.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_username_role", columnList = "username, role"),
        @Index(name = "idx_role_username", columnList = "role, username")
})
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

    @Column(name = "novel_count", nullable = false)
    private int novelCount;

    @Column(name = "chapter_count", nullable = false)
    private int chapterCount;

    @Column(name = "word_count", nullable = false)
    private int wordCount;

    @Column(name = "unread_count", nullable = false)
    private int unreadCount;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
