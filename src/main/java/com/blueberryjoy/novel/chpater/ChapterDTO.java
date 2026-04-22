package com.blueberryjoy.novel.chpater;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChapterDTO(Long id, String title, String content_url, BigDecimal orderIndex, int viewCount, int wordCount,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
}
