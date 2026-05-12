package com.blueberryjoy.novel.chpater;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ChapterDTO {
    private final Long id;
    private final Long novelId;
    private final String title;
    private final String content;
    private final String content_url;
    private final BigDecimal orderIndex;
    private final int viewCount;
    private final int wordCount;
    private final boolean published;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ChapterDTO(Chapter chapter) {
        this.id = chapter.getId();
        this.novelId = chapter.getNovel().getId();
        this.title = chapter.getTitle();
        this.content = chapter.getContent();
        this.content_url = chapter.getContentUrl();
        this.orderIndex = chapter.getOrderIndex();
        this.viewCount = chapter.getViewCount();
        this.wordCount = chapter.getWordCount();
        this.published = chapter.isPublished();
        this.createdAt = chapter.getCreatedAt();
        this.updatedAt = chapter.getUpdatedAt();
    }

    public ChapterDTO(long id, long novelId, String title, BigDecimal orderIndex, boolean published) {
        this.id = id;
        this.novelId = novelId;
        this.title = title;
        this.content = null;
        this.content_url = null;
        this.orderIndex = orderIndex;
        this.viewCount = -1;
        this.wordCount = -1;
        this.published = published;
        this.createdAt = null;
        this.updatedAt = null;
    }
}
