package com.blueberryjoy.novel.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Modifying
    @Query("UPDATE Tag t SET t.tagUsage = t.tagUsage - 1 WHERE t.id IN :tagIds")
    void decreaseCount(List<Long> tagIds);

    @Query("SELECT new com.blueberryjoy.novel.tag.TagDTO(t.id, t.name, t.tagUsage) FROM Tag t")
    List<TagDTO> findAllDTO();
}