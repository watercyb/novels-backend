package com.blueberryjoy.novel.message.template;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.comment.Comment;
import com.blueberryjoy.novel.user.User;

public class ChapterCommentMessageDTO {
    Long userId = null;
    String username = null;
    Long chapterId = null;
    String title = null;
    Long commentId = null;
    String commentContent = null;

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
