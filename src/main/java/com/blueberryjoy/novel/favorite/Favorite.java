package com.blueberryjoy.novel.favorite;

import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "novel_id"}),
        indexes = {
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name = "idx_novel", columnList = "novel_id"),
                @Index(name = "idx_user_novel", columnList = "user_id, novel_id"),
                @Index(name = "idx_user_create", columnList = "user_id, created_at DESC")
        }
)
@Getter
@Setter
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novel novel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
