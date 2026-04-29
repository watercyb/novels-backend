package com.blueberryjoy.novel.tag;

import com.blueberryjoy.novel.novel.Novel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "tags",
        indexes = {
                @Index(name = "idx_name", columnList = "tag_name"),
                @Index(name = "idx_nameCN", columnList = "tag_name_cn")
        }
)
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "tag_name", unique = true, nullable = false)
    private String name;

    @Column(name = "tag_name_cn", unique = true, nullable = false)
    private String nameCN;

    @Column(name = "tag_usage", nullable = false)
    private int tagUsage;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Novel> novels = new HashSet<>();

    public void updateCount(int count) {
        tagUsage += count;
    }
}
