package com.blueberryjoy.novel.message;

import java.time.LocalDateTime;

public class MessageDTO {
    Long id;
    Long senderId;
    Long recipientId;
    String messageContent;
    Message.MessageType type;
    boolean read;
    LocalDateTime createdAt;

    public MessageDTO(Message message) {
        id = message.getId();
        senderId = message.getSenderId();
        recipientId = message.getRecipientId();
        messageContent = message.getMessageContent();
        type = message.getType();
        read = message.isRead();
        createdAt = message.getCreatedAt();
    }

    public MessageDTO(Message message, String messageContent) {
        id = message.getId();
        senderId = message.getSenderId();
        recipientId = message.getRecipientId();
        this.messageContent = messageContent;
        type = message.getType();
        read = message.isRead();
        createdAt = message.getCreatedAt();
    }
}