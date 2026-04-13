package com.blueberryjoy.novel.novel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NovelRepository extends JpaRepository<Novel, Long> {
    @Query("SELECT n FROM Novel n LEFT JOIN FETCH n.tags WHERE n.author.id = :userId")
    List<Novel> findNovels(@Param("userId") Long userId);
}