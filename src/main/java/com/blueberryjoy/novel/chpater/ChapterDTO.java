package com.blueberryjoy.novel.chpater;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ChapterDTO {
    private final Long id;
    private final String title;
    private final String content_url;
    private final BigDecimal orderIndex;
    private final int viewCount;
    private final int wordCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ChapterDTO(Chapter chapter) {
        this.id = chapter.getId();
        this.title = chapter.getTitle();
        this.content_url = chapter.getContentUrl();
        this.orderIndex = chapter.getOrderIndex();
        this.viewCount = chapter.getViewCount();
        this.wordCount = chapter.getWordCount();
        this.createdAt = chapter.getCreatedAt();
        this.updatedAt = chapter.getUpdatedAt();
    }

    public ChapterDTO(long id, String title, String contentUrl, BigDecimal orderIndex, int viewCount, int wordCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content_url = contentUrl;
        this.orderIndex = orderIndex;
        this.viewCount = viewCount;
        this.wordCount = wordCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
