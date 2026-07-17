package com.blueberryjoy.novel.novel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NovelRepository extends JpaRepository<Novel, Long> {
    @Query("SELECT n FROM Novel n LEFT JOIN FETCH n.tags WHERE n.author.id = :userId")
    List<Novel> queryNovelsByAuthor(@Param("userId") Long userId);

    @Query(value = """
                SELECT DISTINCT n.*,
                MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AS score
                FROM novels n FORCE INDEX(ft_title)
                JOIN novel_tags nt ON n.novel_id = nt.novel_id
                WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND nt.tag_id IN (:tags)
                ORDER BY score DESC
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n FORCE INDEX(ft_title)
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND nt.tag_id IN (:tags)
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordAndTagChinese(String keyword, List<Long> tags, Pageable pageable);

    @Query(value = """
                SELECT DISTINCT n.*,
                MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AS score
                FROM novels n FORCE INDEX(ft_title)
                JOIN novel_tags nt ON n.novel_id = nt.novel_id
                WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                  AND nt.tag_id IN (:tags)
                  AND n.type = :typeStr
                ORDER BY score DESC
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n FORCE INDEX(ft_title)
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                      AND nt.tag_id IN (:tags)
                      AND n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordTagAndTypeChinese(
            String keyword,
            List<Long> tags,
            String typeStr,
            Pageable pageable
    );

    @Query(value = """
                SELECT DISTINCT n.*
                FROM novels n FORCE INDEX(ft_title)
                JOIN novel_tags nt ON n.novel_id = nt.novel_id
                WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND nt.tag_id IN (:tags)
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n FORCE INDEX(ft_title)
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND nt.tag_id IN (:tags)
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordAndTagChineseSort(String keyword, List<Long> tags, Pageable pageable);

    @Query(value = """
                SELECT DISTINCT n.*
                FROM novels n FORCE INDEX(ft_title)
                JOIN novel_tags nt ON n.novel_id = nt.novel_id
                WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                  AND nt.tag_id IN (:tags)
                  AND n.type = :typeStr
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n FORCE INDEX(ft_title)
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                      AND nt.tag_id IN (:tags)
                      AND n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordTagAndTypeChineseSort(
            String keyword,
            List<Long> tags,
            String typeStr,
            Pageable pageable
    );

    @Query(value = """
            SELECT n.*,
                   MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AS score
            FROM novels n FORCE INDEX(ft_title)
            WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
            ORDER BY score DESC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM novels n FORCE INDEX(ft_title)
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordChinese(String keyword, Pageable pageable);

    @Query(value = """
            SELECT n.*,
                   MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AS score
            FROM novels n FORCE INDEX(ft_title)
            WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND n.type = :typeStr
            ORDER BY score DESC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM novels n FORCE INDEX(ft_title)
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordAndTypeChinese(String keyword, String typeStr, Pageable pageable);

    @Query(value = """
            SELECT n.*
            FROM novels n FORCE INDEX(ft_title)
            WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM novels n FORCE INDEX(ft_title)
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordChineseSort(String keyword, Pageable pageable);

    @Query(value = """
            SELECT n.*
            FROM novels n FORCE INDEX(ft_title)
            WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND n.type = :typeStr
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM novels n FORCE INDEX(ft_title)
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordAndTypeChineseSort(String keyword, String typeStr, Pageable pageable);

    @Query(value = """
                SELECT DISTINCT n.*,
                MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AS score
                FROM novels n FORCE INDEX(ft_title_en)
                JOIN novel_tags nt ON n.novel_id = nt.novel_id
                WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND nt.tag_id IN (:tags)
                ORDER BY score DESC
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n FORCE INDEX(ft_title_en)
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND nt.tag_id IN (:tags)
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordAndTagEnglish(String keyword, List<Long> tags, Pageable pageable);

    @Query(value = """
                SELECT DISTINCT n.*,
                MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AS score
                FROM novels n FORCE INDEX(ft_title_en)
                JOIN novel_tags nt ON n.novel_id = nt.novel_id
                WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                  AND nt.tag_id IN (:tags)
                  AND n.type = :typeStr
                ORDER BY score DESC
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n FORCE INDEX(ft_title_en)
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                      AND nt.tag_id IN (:tags)
                      AND n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordTagAndTypeEnglish(
            String keyword,
            List<Long> tags,
            String typeStr,
            Pageable pageable
    );

    @Query(value = """
                SELECT DISTINCT n.*
                FROM novels n FORCE INDEX(ft_title_en)
                JOIN novel_tags nt ON n.novel_id = nt.novel_id
                WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND nt.tag_id IN (:tags)
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n FORCE INDEX(ft_title_en)
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND nt.tag_id IN (:tags)
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordAndTagEnglishSort(String keyword, List<Long> tags, Pageable pageable);

    @Query(value = """
                SELECT DISTINCT n.*
                FROM novels n FORCE INDEX(ft_title_en)
                JOIN novel_tags nt ON n.novel_id = nt.novel_id
                WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                  AND nt.tag_id IN (:tags)
                  AND n.type = :typeStr
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n FORCE INDEX(ft_title_en)
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                      AND nt.tag_id IN (:tags)
                      AND n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordTagAndTypeEnglishSort(
            String keyword,
            List<Long> tags,
            String typeStr,
            Pageable pageable
    );

    @Query(value = """
            SELECT n.*,
                   MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AS score
            FROM novels n FORCE INDEX(ft_title_en)
            WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
            ORDER BY score DESC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM novels n FORCE INDEX(ft_title_en)
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordEnglish(String keyword, Pageable pageable);

    @Query(value = """
            SELECT n.*,
                   MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AS score
            FROM novels n FORCE INDEX(ft_title_en)
            WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND n.type = :typeStr
            ORDER BY score DESC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM novels n FORCE INDEX(ft_title_en)
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordAndTypeEnglish(String keyword, String typeStr, Pageable pageable);

    @Query(value = """
            SELECT n.*
            FROM novels n FORCE INDEX(ft_title_en)
            WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM novels n FORCE INDEX(ft_title_en)
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE)
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordEnglishSort(String keyword, Pageable pageable);

    @Query(value = """
            SELECT n.*
            FROM novels n FORCE INDEX(ft_title_en)
            WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND n.type = :typeStr
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM novels n FORCE INDEX(ft_title_en)
                    WHERE MATCH(n.title) AGAINST(:keyword IN BOOLEAN MODE) AND n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> searchByKeywordAndTypeEnglishSort(String keyword, String typeStr, Pageable pageable);

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
            JOIN novel_tags nt ON n.novel_id = nt.novel_id
            WHERE nt.tag_id IN (:tags) AND n.type = :typeStr
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n
                    JOIN novel_tags nt ON n.novel_id = nt.novel_id
                    WHERE nt.tag_id IN (:tags) AND n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> searchByTagsAndType(List<Long> tags, String typeStr, Pageable pageable);

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
            WHERE n.user_id = :userId AND n.type = :typeStr
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n
                    WHERE n.user_id = :userId AND n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> searchByUseIdAndType(long userId, String typeStr, Pageable pageable);

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


    @Query(value = """
            SELECT DISTINCT n.*
            FROM novels n
            WHERE n.type = :typeStr
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT n.novel_id)
                    FROM novels n
                    WHERE n.type = :typeStr
                    """,
            nativeQuery = true)
    Page<Novel> queryAllNovelsByType(String typeStr, Pageable pageable);

    @Query("SELECT n FROM Novel n WHERE n.id IN (:ids)")
    List<Novel> queryNovelsByIds(@Param("ids") List<Long> ids);

    @Query(value = """
            SELECT DISTINCT n.*
            FROM novels n
            ORDER BY last_updated_at DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Novel> queryRecent10Novels();
}