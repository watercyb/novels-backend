package com.blueberryjoy.novel.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            SELECT c
            FROM Comment c
            JOIN FETCH c.user
            WHERE c.chapter = :chapterId
            ORDER BY c.createdAt DESC
            """)
    Page<Comment> findCommentsByChapter(@Param("chapterId") Long chapterId, Pageable pageable);

    @Query("""
            SELECT c
            FROM Comment c
            JOIN FETCH c.user
            WHERE c.parent = :parentId
            ORDER BY c.createdAt DESC
            """)
    Page<Comment> findCommentsByParentId(@Param("commentId") Long parentId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :commendId")
    void deleteComment(@Param("commendId") Long commendId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.parent.id = :parentId")
    void deleteCommentsByParentId(@Param("parentId") Long parentId);

    @Modifying
    @Query(value = """
                DELETE c
                FROM comments c
                JOIN comments p ON c.parent_id = p.comment_id
                WHERE p.chapter_id = :chapterId
            """, nativeQuery = true)
    void deleteRepliesByChapter(@Param("chapterId") Long chapterId);

    @Modifying
    @Query("""
                DELETE FROM Comment c
                WHERE c.chapter.id = :chapterId
            """)
    void deleteCommentsByChapter(@Param("chapterId") Long chapterId);

    @Modifying
    @Query(value = """
                DELETE c
                FROM comments c
                JOIN comments p ON c.parent_id = p.comment_id
                JOIN chapters ch ON p.chapter_id = ch.chapter_id
                WHERE ch.novel_id = :novelId
            """, nativeQuery = true)
    void deleteRepliesByNovel(@Param("novelId") Long novelId);

    @Modifying
    @Query(value = """
                DELETE c
                FROM comments c
                JOIN chapters ch ON c.chapter_id = ch.chapter_id
                WHERE ch.novel_id = :novelId
            """, nativeQuery = true)
    void deleteCommentsByNovel(@Param("novelId") Long novelId);
}