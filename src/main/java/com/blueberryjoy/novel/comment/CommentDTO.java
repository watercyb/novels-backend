package com.blueberryjoy.novel.comment;

import java.time.LocalDateTime;

public record CommentDTO(Long id, Long userId, Long chapterId, Long parentId, String content, int commentCount,
                         LocalDateTime createdAt) {
}