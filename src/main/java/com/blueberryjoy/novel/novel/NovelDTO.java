package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.tag.Tag;
import com.blueberryjoy.novel.tag.TagDTO;
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
    private String author;
    private String coverUrl;
    private List<TagDTO> tags;
    private int likeCount;
    private int chapterCount;
    private int wordCount;
    private Novel.Type type;
    private String lastChapterTitle;
    private LocalDateTime lastUpdatedAt;
    private LocalDateTime createdAt;

    public NovelDTO(Novel novel) {
        id = novel.getId();
        title = novel.getTitle();
        description = novel.getDescription();
        userId = novel.getAuthor().getId();
        author = novel.getAuthor().getUsername();
        coverUrl = novel.getCoverUrl();
        tags = new ArrayList<>();
        for (Tag tag : novel.getTags()) {
            tags.add(new TagDTO(tag));
        }
        likeCount = novel.getLikeCount();
        chapterCount = novel.getChapterCount();
        wordCount = novel.getWordCount();
        type = novel.getType();
        lastChapterTitle = novel.getLastChapterTitle();
        lastUpdatedAt = novel.getLastUpdatedAt();
        createdAt = novel.getCreatedAt();
    }

    public String toString() {
        return "ID: " + id + " Title: " + title + " Tags: " + tags;
    }
}
