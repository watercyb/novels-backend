package com.blueberryjoy.novel.novel;

import java.time.LocalDateTime;

public record NovelPreviewDTO(Long id, String title, String coverUrl, LocalDateTime createdAt) {
}
