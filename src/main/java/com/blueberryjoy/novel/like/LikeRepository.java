package com.blueberryjoy.novel.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("SELECT l.novel.id FROM Like l WHERE l.user.id = :userId")
    List<Long> findLikedNovelIdsByUserId(Long userId);

    boolean existsByUserIdAndNovelId(Long userId, Long novelId);

    Like findByUserIdAndNovelId(Long userId, Long novelId);

    @Modifying
    @Query("""
                DELETE FROM Like l
                WHERE l.novel.id = :novelId
            """)
    void deleteLikesByNovelId(@Param("novelId") Long novelId);
}