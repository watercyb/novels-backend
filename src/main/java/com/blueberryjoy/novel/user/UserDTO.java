package com.blueberryjoy.novel.user;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String avatarUrl;
    private final int novels;
    private final int chapters;
    private final int words;
    private final LocalDateTime createdAt;

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        avatarUrl = user.getAvatarUrl();
        novels = user.getNovels();
        chapters = user.getChapters();
        words = user.getWords();
        createdAt = user.getCreatedAt();
    }
}
