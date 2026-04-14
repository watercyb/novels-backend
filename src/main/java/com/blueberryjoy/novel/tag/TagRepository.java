package com.blueberryjoy.novel.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Modifying
    @Query("UPDATE Tag t SET t.tagCount = t.tagCount - 1 WHERE t.id IN :tagIds")
    void decreaseCount(List<Long> tagIds);
}