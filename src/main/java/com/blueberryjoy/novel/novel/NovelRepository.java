package com.blueberryjoy.novel.novel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NovelRepository extends JpaRepository<Novel, Long> {
    @Query("SELECT n FROM Novel n LEFT JOIN FETCH n.tags WHERE n.author.id = :userId")
    List<Novel> findNovels(@Param("userId") Long userId);

    @Query(value = """
                SELECT DISTINCT n.*,
                MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AS score
                FROM novels n
                JOIN novel_tags nt ON n.novel_id = nt.novel_id
                WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND nt.tag_id IN (:tags)
                ORDER BY score DESC
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND nt.tag_id IN (:tags)
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordAndTag(String keyword, List<Long> tags, Pageable pageable);

    @Query(value = """
            SELECT n.*,
                   MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AS score
            FROM novels n
            WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
            ORDER BY score DESC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM novels n
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeyword(String keyword, Pageable pageable);

    @Query(value = """
            SELECT DISTINCT n.*
            FROM novels n
            JOIN novel_tags nt ON n.novel_id = nt.novel_id
            WHERE nt.tag_id IN (:tags)
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE nt.tag_id IN (:tags)
                    """,
            nativeQuery = true)
    Page<Novel> searchByTags(List<Long> tags, Pageable pageable);

    @Query(value = """
            SELECT DISTINCT n.*
            FROM novels n
            WHERE n.user_id = :userId
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n
                    WHERE n.user_id = :userId
                    """,
            nativeQuery = true)
    Page<Novel> searchByUseId(long userId, Pageable pageable);

    @Query(value = """
            SELECT DISTINCT n.*
            FROM novels n
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n
                    """,
            nativeQuery = true)
    Page<Novel> queryAllNovels(Pageable pageable);
}