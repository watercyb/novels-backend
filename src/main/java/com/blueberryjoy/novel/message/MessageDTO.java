package com.blueberryjoy.novel.message;

import com.blueberryjoy.novel.user.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long senderId;
    private UserDTO sender;
    private Long recipientId;
    private String messageContent;
    private Message.MessageType type;
    private boolean read;
    private LocalDateTime createdAt;

    public MessageDTO(Message message, UserDTO sender) {
        id = message.getId();
        senderId = message.getSenderId();
        this.sender = sender;
        recipientId = message.getRecipientId();
        messageContent = message.getMessageContent();
        type = message.getType();
        read = message.isRead();
        createdAt = message.getCreatedAt();
    }

    public MessageDTO(Message message, String messageContent, UserDTO sender) {
        id = message.getId();
        senderId = message.getSenderId();
        this.sender = sender;
        recipientId = message.getRecipientId();
        this.messageContent = messageContent;
        type = message.getType();
        read = message.isRead();
        createdAt = message.getCreatedAt();
    }
}