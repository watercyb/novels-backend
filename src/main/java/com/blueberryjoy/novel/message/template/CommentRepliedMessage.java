package com.blueberryjoy.novel.message.template;

public record CommentRepliedMessage(Long userId, Long parentId, Long replyId) {
}