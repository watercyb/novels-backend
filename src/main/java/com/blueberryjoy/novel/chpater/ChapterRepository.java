package com.blueberryjoy.novel.chpater;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    @Query("""
            SELECT new com.blueberryjoy.novel.chpater.ChapterDTO(
                c.id, c.title, c.contentUrl, c.orderIndex, c.viewCount, c.wordCount, c.createdAt, c.updatedAt
            )
            FROM Chapter c
            WHERE c.novel.id = :novelId
            ORDER BY c.orderIndex ASC
            """)
    Page<ChapterDTO> findChapterPreview(@Param("novelId") Long novelId, Pageable pageable);

    @Query("SELECT MAX(c.orderIndex) FROM Chapter c WHERE c.novel.id = :novelId")
    BigDecimal findLastOrderIndex(@Param("novelId") Long novelId);

    @Modifying
    @Query("""
            UPDATE Chapter c
            SET c.orderIndex = c.orderIndex + 1
            WHERE c.novel.id = :novelId
            AND c.orderIndex >= :startIndex
            """)
    void shiftOrderIndexes(
            @Param("novelId") Long novelId,
            @Param("startIndex") BigDecimal startIndex
    );

    @Modifying
    @Query("DELETE FROM Chapter c WHERE c.novel.id = :novelId")
    void deleteByNovelId(Long novelId);

    @Query(value = """
                SELECT *
                FROM chapters c
                WHERE c.novel_id = :novelId
                  AND c.order_index > :orderIndex
                ORDER BY c.order_index ASC
                LIMIT 1
            """, nativeQuery = true)
    Chapter findNextChapter(
            @Param("novelId") Long novelId,
            @Param("orderIndex") BigDecimal orderIndex
    );
}