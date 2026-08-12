package com.blueberryjoy.novel.message.template;

public record ChapterCommentMessage(Long userId, Long chapterId, Long commentId) {
}