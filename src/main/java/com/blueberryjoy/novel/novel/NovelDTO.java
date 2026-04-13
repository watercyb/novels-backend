package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.tag.Tag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class NovelDTO {
    private long id;
    private String title;
    private String description;
    private long userId;
    private String coverUrl;
    private List<String> tags;
    private LocalDateTime createdAt;

    public NovelDTO(Novel novel) {
        id = novel.getId();
        title = novel.getTitle();
        description = novel.getDescription();
        userId = novel.getAuthor().getId();
        coverUrl = novel.getCoverUrl();
        tags = new ArrayList<>();
        for (Tag tag : novel.getTags()) {
            tags.add(tag.getName());
        }
        createdAt = novel.getCreatedAt();
    }
}
