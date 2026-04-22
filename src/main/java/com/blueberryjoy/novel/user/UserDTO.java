package com.blueberryjoy.novel.user;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String avatarUrl;
    private final int novelCount;
    private final int chapterCount;
    private final int wordCount;
    private final LocalDateTime createdAt;

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        avatarUrl = user.getAvatarUrl();
        novelCount = user.getNovelCount();
        chapterCount = user.getChapterCount();
        wordCount = user.getWordCount();
        createdAt = user.getCreatedAt();
    }
}
