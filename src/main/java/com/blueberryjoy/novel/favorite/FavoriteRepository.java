package com.blueberryjoy.novel.favorite;

import com.blueberryjoy.novel.novel.Novel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f.novel.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findFavoriteNovelIdsByUserId(Long userId);

    @Query("""
                SELECT f.novel
                FROM Favorite f
                WHERE f.user.id = :userId
            """)
    Page<Novel> findFavoriteNovelsByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndNovelId(Long userId, Long novelId);

    @Modifying
    @Query("""
                DELETE FROM Favorite f
                WHERE f.novel.id = :novelId
            """)
    void deleteFavoritesByNovelId(@Param("novelId") Long novelId);
}
