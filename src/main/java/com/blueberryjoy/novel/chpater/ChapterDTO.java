package com.blueberryjoy.novel.chpater;

import com.blueberryjoy.novel.user.User;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChapterDTO {
    private final Long id;
    private final String novelTitle;
    private final Long novelId;
    private final String author;
    private final Long authorId;
    private final String title;
    private String content;
    private final BigDecimal orderIndex;
    private final int viewCount;
    private final int wordCount;
    private final boolean published;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ChapterDTO(Chapter chapter, boolean includeAuthorAndNovel) {
        this.id = chapter.getId();
        this.novelId = chapter.getNovel().getId();
        this.title = chapter.getTitle();
        this.content = chapter.getContent();
        this.orderIndex = chapter.getOrderIndex();
        this.viewCount = chapter.getViewCount();
        this.wordCount = chapter.getWordCount();
        this.published = chapter.isPublished();
        this.createdAt = chapter.getCreatedAt();
        this.updatedAt = chapter.getUpdatedAt();
        if (includeAuthorAndNovel) {
            User author = chapter.getNovel().getAuthor();
            this.author = author.getUsername();
            this.authorId = author.getId();
            this.novelTitle = chapter.getNovel().getTitle();
        } else {
            this.author = "";
            this.authorId = -1L;
            this.novelTitle = null;
        }
    }

    public ChapterDTO(long id, long novelId, String title, BigDecimal orderIndex, int wordCount, boolean published) {
        this.id = id;
        this.novelTitle = null;
        this.novelId = novelId;
        this.author = "";
        this.authorId = -1L;
        this.title = title;
        this.content = null;
        this.orderIndex = orderIndex;
        this.viewCount = -1;
        this.wordCount = wordCount;
        this.published = published;
        this.createdAt = null;
        this.updatedAt = null;
    }
}
