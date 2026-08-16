package com.blueberryjoy.novel.message.template;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.comment.Comment;
import com.blueberryjoy.novel.user.User;
import lombok.Data;

@Data
public class ChapterCommentMessageDTO {
    private Long userId = null;
    private String username = null;
    private Long chapterId = null;
    private String title = null;
    private Long commentId = null;
    private String commentContent = null;

    public ChapterCommentMessageDTO(User user, Chapter chapter, Comment comment) {
        if (user != null) {
            userId = user.getId();
            username = user.getUsername();
        }
        if (chapter != null) {
            chapterId = chapter.getId();
            title = chapter.getTitle();
        }
        if (comment != null) {
            commentId = comment.getId();
            commentContent = comment.getContent();
        }
    }
}
