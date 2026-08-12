package com.blueberryjoy.novel.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Modifying
    @Query("""
                UPDATE Message m
                SET m.read = :read
                WHERE m.id = :id
            """)
    int updateReadStatus(
            @Param("id") Long id,
            @Param("read") boolean read
    );
}