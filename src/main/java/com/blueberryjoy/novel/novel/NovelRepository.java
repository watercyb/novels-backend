package com.blueberryjoy.novel.novel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NovelRepository extends JpaRepository<Novel, Long> {
    @Query("""
            SELECT new com.blueberryjoy.novel.novel.NovelPreviewDTO(
                n.id, n.title, n.coverUrl, n.createdAt
            )
            FROM Novel n
            WHERE n.author.id = :userId
            ORDER BY n.createdAt DESC
            """)
    List<NovelPreviewDTO> findNovelPreview(@Param("userId") Long userId);
}