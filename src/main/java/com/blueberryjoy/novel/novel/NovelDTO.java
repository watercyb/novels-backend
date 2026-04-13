package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.tag.Tag;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class NovelDTO {
    private long id;
    private String title;
    private String description;
    private long userId;
    private String coverUrl;
    private List<String> tags;
    private int likes;
    private int chapters;
    private int words;
    private String lastChapterTitle;
    private LocalDateTime lastUpdatedAt;
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
        likes = novel.getLikes();
        chapters = novel.getChapters();
        words = novel.getWords();
        lastChapterTitle = novel.getLastChapterTitle();
        lastUpdatedAt = novel.getLastUpdatedAt();
        createdAt = novel.getCreatedAt();
    }
}
