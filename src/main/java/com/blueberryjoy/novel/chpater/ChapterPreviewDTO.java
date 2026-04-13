package com.blueberryjoy.novel.chpater;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChapterPreviewDTO(Long id, String title, String content_url, BigDecimal orderIndex, int views,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
}
