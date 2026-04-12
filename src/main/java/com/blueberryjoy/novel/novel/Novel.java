package com.blueberryjoy.novel.novel;
import com.blueberryjoy.novel.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "novels")
@Data
public class Novel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long novelId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    private String coverUrl;

    private java.time.LocalDateTime createdAt;
}
