package com.blueberryjoy.novel.chpater;
import com.blueberryjoy.novel.novel.Novel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chapters")
@Data
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chapterId;

    @ManyToOne
    @JoinColumn(name = "novel_id")
    private Novel novel;

    private String title;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    private Integer orderIndex;

    private java.time.LocalDateTime createdAt;
}
