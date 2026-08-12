package com.blueberryjoy.novel.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    Optional<Tag> findByNameCN(String nameCN);

    @Modifying
    @Query("UPDATE Tag t SET t.tagUsage = t.tagUsage - 1 WHERE t.id IN :tagIds")
    void decreaseCount(List<Long> tagIds);

    @Query("SELECT new com.blueberryjoy.novel.tag.TagDTO(t.id, t.name, t.nameCN, t.tagUsage) FROM Tag t")
    List<TagDTO> findAllDTO();

    @Modifying
    @Query("""
                UPDATE Tag t
                SET t.tagUsage = t.tagUsage + 1
                WHERE t.id IN :ids
            """)
    void incrementTagUsage(Set<Long> ids);

    @Modifying
    @Query("""
                UPDATE Tag t
                SET t.tagUsage = t.tagUsage - 1
                WHERE t.id IN :ids
            """)
    void decrementTagUsage(Set<Long> ids);

    @Modifying
    @Query("""
                UPDATE Tag t
                SET t.tagUsage = t.tagUsage + :n
                WHERE t.id = :tagId
            """)
    void updateTagUsage(
            @Param("tagId") Long tagId,
            @Param("n") int n
    );
}