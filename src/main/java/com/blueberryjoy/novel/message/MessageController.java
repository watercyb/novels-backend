/*
Post /{messageId} (isAuthenticated()) PathVariable: messageId
Get / (isAuthenticated()) RequestParam: page(Integer), size(Integer) -> PageDTO<MessageDTO>
Get /{messageId} (isAuthenticated()) PathVariable: messageId -> MessageDTO
Delete /{messageId} (isAuthenticated()) PathVariable: messageId
*/

package com.blueberryjoy.novel.message;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.novel.PageDTO;
import com.blueberryjoy.novel.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> readMessage(@PathVariable Long messageId) {
        try {
            if (messageId == null) throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            messageService.readMessage(user, messageId);
            return ResponseEntity.ok().body(new MessageResponse(true, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMessages(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (page == null) page = 0;
            if (size == null) size = 20;
            Pageable pageable = PageRequest.of(page, size);
            Page<MessageDTO> messages = messageService.searchByRecipientId(user.getId(), pageable);
            return ResponseEntity.ok().body(new MessageResponse(true, null, null, new PageDTO<>(messages)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMessage(@PathVariable Long messageId) {
        try {
            if (messageId == null) throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            MessageDTO messageDTO = messageService.getMessage(user, messageId);
            return ResponseEntity.ok().body(new MessageResponse(true, null, messageDTO, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
        try {
            if (messageId == null) throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            messageService.deleteMessage(user, messageId);
            return ResponseEntity.ok().body(new MessageResponse(true, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record MessageResponse(boolean success, String message, MessageDTO messageDTO, PageDTO<MessageDTO> page) {
    }
}
