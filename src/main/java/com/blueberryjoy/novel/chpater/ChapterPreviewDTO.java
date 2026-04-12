package com.blueberryjoy.novel.chpater;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChapterPreviewDTO(Long id, String title, String content_url, BigDecimal orderIndex,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
}
