package com.blueberryjoy.novel.user;

import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelDTO;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UserDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String avatarUrl;
    private final int novelCount;
    private final int chapterCount;
    private final int wordCount;
    private final List<NovelDTO> novels;
    private final User.Role role;
    private final LocalDateTime createdAt;

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        avatarUrl = user.getAvatarUrl();
        novelCount = user.getNovelCount();
        chapterCount = user.getChapterCount();
        wordCount = user.getWordCount();
        novels = null;
        role = user.getRole();
        createdAt = user.getCreatedAt();
    }

    public UserDTO(User user, List<Novel> novels) {
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        avatarUrl = user.getAvatarUrl();
        novelCount = user.getNovelCount();
        chapterCount = user.getChapterCount();
        wordCount = user.getWordCount();
        this.novels = novels.stream().map(NovelDTO::new).toList();
        role = user.getRole();
        createdAt = user.getCreatedAt();
    }
}
