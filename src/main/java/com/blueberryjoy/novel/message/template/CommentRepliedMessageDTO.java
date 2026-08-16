package com.blueberryjoy.novel.message.template;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.comment.Comment;
import com.blueberryjoy.novel.user.User;
import lombok.Data;

@Data
public class CommentRepliedMessageDTO {
    private Long userId = null;
    private String username = null;
    private Long parentId = null;
    private String parentCommentContent = null;
    private Long chapterId = null;
    private String title = null;
    private Long replyId = null;
    private String replyCommentContent = null;

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
