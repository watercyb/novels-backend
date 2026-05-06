package com.blueberryjoy.novel.comment;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDTO {
    private final Long id;
    private final Long userId;
    private final String username;
    private final String userAvatarUrl;
    private final Long chapterId;
    private final Long parentId;
    private final String content;
    private final int commentCount;
    private final LocalDateTime createdAt;

    public CommentDTO(Comment comment) {
        id = comment.getId();
        userId = comment.getUser().getId();
        username = comment.getUser().getUsername();
        userAvatarUrl = comment.getUser().getAvatarUrl();
        if (comment.getChapter() != null) {
            chapterId = comment.getChapter().getId();
        } else {
            chapterId = null;
        }
        if (comment.getParent() != null) {
            parentId = comment.getParent().getId();
        } else {
            parentId = null;
        }
        content = comment.getContent();
        commentCount = comment.getCommentCount();
        createdAt = comment.getCreatedAt();
    }
}