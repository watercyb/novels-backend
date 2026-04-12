package com.blueberryjoy.novel.comment;
import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    private java.time.LocalDateTime createdAt;
}
