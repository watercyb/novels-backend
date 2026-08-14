package com.blueberryjoy.novel.message;

import com.blueberryjoy.novel.novel.TypeCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    @Query("""
                SELECT m
                FROM Message m
                WHERE m.recipientId = :recipientId
                ORDER BY m.createdAt DESC
            """)
    Page<Message> findByRecipientId(
            @Param("recipientId") Long recipientId,
            Pageable pageable
    );

    @Query("""
                SELECT COUNT(m.id) AS count
                FROM Message m
                WHERE m.recipientId = -1 and m.read=false
            """)
    int countUnreadSystemMessages();

    @Query("""
                SELECT COUNT(m.id) AS count
                FROM Message m
                WHERE m.senderId = :userId and m.recipientId = -1 and type='UPDATE_ROLE_REQUEST'
            """)
    int checkUpdateRoleRequest(@Param("userId") Long userId);
}