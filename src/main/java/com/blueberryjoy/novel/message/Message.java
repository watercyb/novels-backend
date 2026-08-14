package com.blueberryjoy.novel.message;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_message_sender", columnList = "sender_id"),
        @Index(name = "idx_message_recipient", columnList = "recipient_id"),
        @Index(name = "idx_message_sender_recipient_type", columnList = "sender_id, recipient_id, type"),
        @Index(name = "idx_message_recipient_read", columnList = "recipient_id, is_read")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MessageType type;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum MessageType {
        SYSTEM /*All content are internal translation key*/, USER, CHAPTER_COMMENT, COMMENT_REPLIED, UPDATE_ROLE_REQUEST, DELETION;

        public static MessageType getType(String value) {
            if (value == null) return null;
            try {
                return MessageType.valueOf(value.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}