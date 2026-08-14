package com.blueberryjoy.novel.message;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.chpater.ChapterRepository;
import com.blueberryjoy.novel.comment.Comment;
import com.blueberryjoy.novel.comment.CommentRepository;
import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.message.template.*;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserDTO;
import com.blueberryjoy.novel.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private CommentRepository commentRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    //CHAPTER_COMMENT
    @Transactional
    public void createChapterCommentMessage(User recipient, User user, Chapter chapter, Comment comment) {
        ChapterCommentMessage data = new ChapterCommentMessage(user.getId(), chapter.getId(), comment.getId());
        String messageContent = objectMapper.writeValueAsString(data);
        Message message = new Message();
        message.setSenderId(-1L);
        message.setRecipientId(recipient.getId());
        userRepository.updateUnreadCount(recipient.getId(), 1);
        message.setMessageContent(messageContent);
        message.setType(Message.MessageType.getType("CHAPTER_COMMENT"));
        messageRepository.save(message);
    }

    public MessageDTO getChapterCommentMessage(Message message) {
        ChapterCommentMessage chapterCommentMessage = objectMapper.readValue(message.getMessageContent(), ChapterCommentMessage.class);
        User user = userRepository.findById(chapterCommentMessage.userId()).orElse(null);
        Chapter chapter = chapterRepository.findById(chapterCommentMessage.chapterId()).orElse(null);
        Comment comment = commentRepository.findById(chapterCommentMessage.commentId()).orElse(null);
        ChapterCommentMessageDTO commentRepliedMessageDTO = new ChapterCommentMessageDTO(user, chapter, comment);
        String messageContent = objectMapper.writeValueAsString(commentRepliedMessageDTO);
        return getMessageDTO(message, messageContent);
    }

    //COMMENT_REPLIED
    @Transactional
    public void createCommentRepliedMessage(User recipient, User user, Comment parent, Comment reply) {
        CommentRepliedMessage data = new CommentRepliedMessage(user.getId(), parent.getId(), reply.getId());
        String messageContent = objectMapper.writeValueAsString(data);
        Message message = new Message();
        message.setSenderId(-1L);
        message.setRecipientId(recipient.getId());
        userRepository.updateUnreadCount(recipient.getId(), 1);
        message.setMessageContent(messageContent);
        message.setType(Message.MessageType.getType("COMMENT_REPLIED"));
        messageRepository.save(message);
    }

    public MessageDTO getCommentRepliedMessage(Message message) {
        CommentRepliedMessage commentRepliedMessage = objectMapper.readValue(message.getMessageContent(), CommentRepliedMessage.class);
        User user = userRepository.findById(commentRepliedMessage.userId()).orElse(null);
        Comment parent = commentRepository.findById(commentRepliedMessage.parentId()).orElse(null);
        Comment reply = commentRepository.findById(commentRepliedMessage.replyId()).orElse(null);
        CommentRepliedMessageDTO commentRepliedMessageDTO = new CommentRepliedMessageDTO(user, parent, reply);
        String messageContent = objectMapper.writeValueAsString(commentRepliedMessageDTO);
        return getMessageDTO(message, messageContent);
    }

    //UPDATE_ROLE_REQUEST
    @Transactional
    public void createUpdateRoleRequestMessage(User sender) {
        Message message = new Message();
        message.setSenderId(sender.getId());
        message.setRecipientId(-1L);
        message.setType(Message.MessageType.getType("UPDATE_ROLE_REQUEST"));
        message.setMessageContent("");
        messageRepository.save(message);
    }

    public MessageDTO getUpdateRoleRequestMessage(Message message) {
        return getMessageDTO(message, null);
    }

    public MessageDTO getMessageByType(Message message) {
        return switch (message.getType()) {
            case SYSTEM -> getSystemMessage(message);
            case USER -> getUserMessage(message);
            case CHAPTER_COMMENT -> getChapterCommentMessage(message);
            case COMMENT_REPLIED -> getCommentRepliedMessage(message);
            case UPDATE_ROLE_REQUEST -> getUpdateRoleRequestMessage(message);
            case DELETION -> getDeletedMessage(message);
        };
    }

    private MessageDTO getMessageDTO(Message message, String messageContent) {
        User user = userRepository.findById(message.getSenderId()).orElse(null);
        UserDTO sender = user != null ? new UserDTO(user) : null;
        if (messageContent == null) {
            return new MessageDTO(message, sender);
        } else {
            return new MessageDTO(message, messageContent, sender);
        }
    }

    public MessageDTO getSystemMessage(Message message) {
        return getMessageDTO(message, null);
    }

    public MessageDTO getUserMessage(Message message) {
        return getMessageDTO(message, null);
    }

    //DELETION
    @Transactional
    public void createDeletedMessage(User recipient, String type, String name, String reason) {
        Message message = new Message();
        message.setSenderId(-1L);
        message.setRecipientId(recipient.getId());
        userRepository.updateUnreadCount(recipient.getId(), 1);
        DeletionMessage.Type deletionMessageType = DeletionMessage.Type.getType(type);
        if (deletionMessageType == null) throw new ApiException("INVALID_INPUT", HttpStatus.BAD_REQUEST);
        DeletionMessage data = new DeletionMessage(deletionMessageType, name, reason);
        String messageContent = objectMapper.writeValueAsString(data);
        message.setMessageContent(messageContent);
        message.setType(Message.MessageType.getType("DELETION"));
        messageRepository.save(message);
    }

    public MessageDTO getDeletedMessage(Message message) {
        return getMessageDTO(message, null);
    }

    @Transactional
    public void readMessage(User user, Long messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) throw new ApiException("UNABLE_FOUND_MESSAGE", HttpStatus.BAD_REQUEST);
        if (!Objects.equals(user.getId(), message.getRecipientId()))
            throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
        if (!message.isRead()) {
            message.setRead(true);
            userRepository.updateUnreadCount(message.getRecipientId(), -1);
            messageRepository.save(message);
        }
    }

    @Transactional
    public void deleteMessage(User user, Long messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null && !user.getId().equals(message.getRecipientId()))
            throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
        if (message != null) {
            if (!message.isRead()) userRepository.updateUnreadCount(message.getRecipientId(), -1);
            messageRepository.delete(message);
        }
    }

    @Transactional
    public Page<MessageDTO> searchByRecipientId(Long recipientId, Pageable pageable) {
        Page<Message> messages = messageRepository.findByRecipientId(recipientId, pageable);
        return messages.map(this::getMessageByType);
    }

    @Transactional
    public MessageDTO getMessage(User user, Long messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null || !user.getId().equals(message.getRecipientId()))
            throw new ApiException("UNABLE_FOUND_MESSAGE", HttpStatus.BAD_REQUEST);
        return getMessageByType(message);
    }

    //SYSTEM
    @Transactional
    public void createSystemMessage(User recipient, String content) {
        Message message = new Message();
        message.setSenderId(-1L);
        message.setRecipientId(recipient.getId());
        userRepository.updateUnreadCount(recipient.getId(), 1);
        message.setMessageContent(content);
        message.setType(Message.MessageType.getType("SYSTEM"));
        messageRepository.save(message);
    }

    //USER
    @Transactional
    public void createUserMessage(User sender, User recipient, String content) {
        Message message = new Message();
        message.setSenderId(sender.getId());
        message.setRecipientId(recipient.getId());
        message.setType(Message.MessageType.getType("USER"));
        message.setMessageContent(content);
        messageRepository.save(message);
    }
}