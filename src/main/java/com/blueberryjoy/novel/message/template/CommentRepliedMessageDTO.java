package com.blueberryjoy.novel.message.template;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.comment.Comment;
import com.blueberryjoy.novel.user.User;

public class CommentRepliedMessageDTO {
    Long userId = null;
    String username = null;
    Long parentId = null;
    String parentCommentContent = null;
    Long chapterId = null;
    String title = null;
    Long replyId = null;
    String replyCommentContent = null;

    public CommentRepliedMessageDTO(User user, Comment parent, Comment reply) {
        if (user != null) {
            userId = user.getId();
            username = user.getUsername();
        }
        if (parent != null) {
            parentId = parent.getId();
            parentCommentContent = parent.getContent();
            Chapter chapter = parent.getChapter();
            chapterId = chapter.getId();
            title = chapter.getTitle();
        }
        if (reply != null) {
            replyId = reply.getId();
            replyCommentContent = reply.getContent();
        }
    }
}
