package com.blueberryjoy.novel.chpater;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    @Query("""
            SELECT new com.blueberryjoy.novel.chpater.ChapterDTO(
                c.id, c.novel.id, c.title, c.orderIndex, c.wordCount, c.published
            )
            FROM Chapter c
            WHERE c.novel.id = :novelId
            ORDER BY c.orderIndex ASC
            """)
    Page<ChapterDTO> findChapterPreview(@Param("novelId") Long novelId, Pageable pageable);

    @Query("""
            SELECT new com.blueberryjoy.novel.chpater.ChapterDTO(
                c.id, c.novel.id, c.title, c.orderIndex, c.wordCount, c.published
            )
            FROM Chapter c
            WHERE c.novel.id = :novelId
                  AND c.published = true
            ORDER BY c.orderIndex ASC
            """)
    Page<ChapterDTO> findPublishedChapterPreview(@Param("novelId") Long novelId, Pageable pageable);

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

    @Query("""
                SELECT new com.blueberryjoy.novel.chpater.ChapterDTO(c.id, c.novel.id, c.title, c.orderIndex, c.wordCount, c.published)
                FROM Chapter c
                WHERE c.novel.id = :novelId
                  AND c.orderIndex < :orderIndex
                ORDER BY c.orderIndex DESC
            """)
    List<ChapterDTO> findPrevChapterDTO(
            @Param("novelId") Long novelId,
            @Param("orderIndex") BigDecimal orderIndex,
            Pageable pageable
    );

    @Query("""
                SELECT new com.blueberryjoy.novel.chpater.ChapterDTO(c.id, c.novel.id, c.title, c.orderIndex, c.wordCount, c.published)
                FROM Chapter c
                WHERE c.novel.id = :novelId
                  AND c.orderIndex < :orderIndex
                  AND c.published = true
                ORDER BY c.orderIndex DESC
            """)
    List<ChapterDTO> findPrevPublishedChapterDTO(
            @Param("novelId") Long novelId,
            @Param("orderIndex") BigDecimal orderIndex,
            Pageable pageable
    );

    @Query("""
                SELECT new com.blueberryjoy.novel.chpater.ChapterDTO(c.id, c.novel.id, c.title, c.orderIndex, c.wordCount, c.published)
                FROM Chapter c
                WHERE c.novel.id = :novelId
                  AND c.orderIndex > :orderIndex
                ORDER BY c.orderIndex ASC
            """)
    List<ChapterDTO> findNextChapterDTO(
            @Param("novelId") Long novelId,
            @Param("orderIndex") BigDecimal orderIndex,
            Pageable pageable
    );

    @Query("""
                SELECT new com.blueberryjoy.novel.chpater.ChapterDTO(c.id, c.novel.id, c.title, c.orderIndex, c.wordCount, c.published)
                FROM Chapter c
                WHERE c.novel.id = :novelId
                  AND c.orderIndex > :orderIndex
                  AND c.published = true
                ORDER BY c.orderIndex ASC
            """)
    List<ChapterDTO> findNextPublishedChapterDTO(
            @Param("novelId") Long novelId,
            @Param("orderIndex") BigDecimal orderIndex,
            Pageable pageable
    );

    @Modifying
    @Query("""
                DELETE FROM Chapter ch
                WHERE ch.novel.id = :novelId
            """)
    void deleteChaptersByNovel(@Param("novelId") Long novelId);

    @Query(value = "SELECT CURRENT_TIMESTAMP", nativeQuery = true)
    LocalDateTime now();

    @Query("""
                SELECT new com.blueberryjoy.novel.chpater.ChapterDTO(c.id, c.novel.id, c.title, c.orderIndex, c.wordCount, c.published)
                FROM Chapter c
                WHERE c.novel.id = :novelId
                ORDER BY c.orderIndex ASC
            """)
    List<ChapterDTO> findChapterDTOByNovelId(@Param("novelId") Long novelId);
}