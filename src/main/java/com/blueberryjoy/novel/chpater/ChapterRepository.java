package com.blueberryjoy.novel.chpater;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    @Query("""
            SELECT new com.blueberryjoy.novel.chpater.ChapterPreviewDTO(
                c.id, c.title, c.contentUrl, c.orderIndex, c.createdAt, c.updatedAt
            )
            FROM Chapter c
            WHERE c.novel.id = :novelId
            ORDER BY c.orderIndex ASC
            """)
    List<ChapterPreviewDTO> findChapterPreview(@Param("novelId") Long novelId);

    @Query("SELECT MAX(c.orderIndex) FROM Chapter c WHERE c.novel.id = :novelId")
    BigDecimal findLastOrderIndex(@Param("novelId") Long novelId);

    @Transactional
    @Modifying
    @Query("UPDATE Chapter c SET c.orderIndex = :newIndex WHERE c.id = :id")
    int updateOrderIndex(@Param("id") Long id,
                         @Param("newIndex") BigDecimal newIndex);

    @Query("SELECT c.novel.id, c.orderIndex FROM Chapter c WHERE c.id = :id")
    Object[] findNovelIdAndOrderIndexById(Long id);

    @Query("""
            SELECT COUNT(c) > 0
            FROM Chapter c
            WHERE c.novel.id = :novelId
            AND c.orderIndex > :aIndex
            AND c.orderIndex < :bIndex
            """)
    boolean existsBetween(
            @Param("novelId") Long novelId,
            @Param("aIndex") BigDecimal aIndex,
            @Param("bIndex") BigDecimal bIndex
    );

    @Modifying
    @Transactional
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
}