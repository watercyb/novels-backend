/*
Delete /novels/{novelId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: novelId, RequestParam: reason(String)
Post /user/{userId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: userId, JSON: {role}
Post /tags (hasAnyAuthority('ADMIN', 'SUPERADMIN')) JSON: {id, name, nameCN, count}
Delete /tags (hasAnyAuthority('ADMIN', 'SUPERADMIN')) JSON: {id}
Delete /user/{userId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: userId, RequestParam: reason(String)
Delete /chapters/{chapterId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: chapterId, RequestParam: reason(String)
Delete /comments/{commentId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: commentId, RequestParam: reason(String)
Get /users RequestParam: id(Long), email(String), username(String), role(String), page(Integer), size(Integer) -> PageDTO<UserDTO>
Get /novels/{novelId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: novelId -> NovelDTO
Post /messages/{messageId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: messageId
Get /messages (hasAnyAuthority('ADMIN', 'SUPERADMIN')) RequestParam: page(Integer), size(Integer) -> PageDTO<MessageDTO>
Get /messages/{messageId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: messageId -> MessageDTO
Delete /messages/{messageId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: messageId
Get /messages/count (hasAnyAuthority('ADMIN', 'SUPERADMIN')) -> Integer
Post / (hasAnyAuthority('ADMIN', 'SUPERADMIN')) JSON: {recipientId, content}
*/


package com.blueberryjoy.novel.admin;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.message.MessageDTO;
import com.blueberryjoy.novel.message.MessageService;
import com.blueberryjoy.novel.novel.NovelDTO;
import com.blueberryjoy.novel.novel.PageDTO;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserDTO;
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

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private MessageService messageService;

    @DeleteMapping("/novels/{novelId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteNovelById(@PathVariable Long novelId, @RequestParam(required = false) String reason) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_DELETE_NOVEL", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            if (novelId == null) throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
            if (reason == null) reason = "";
            adminService.deleteNovelById(adminUser, novelId, reason);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "", null, null, null, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        try {
            String roleStr = (String) body.get("role");
            User.Role newRole = User.Role.getRole(roleStr);
            if (newRole == null)
                throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_UPDATE_ROLE", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            adminService.updateUserRole(adminUser, userId, newRole);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "", null, null, null, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/tags")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> createOrUpdateTag(@RequestBody Map<String, Object> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_CREATE_TAG", HttpStatus.BAD_REQUEST);
            Number id = (Number) body.get("id");
            String name = (String) body.get("name");
            String nameCN = (String) body.get("nameCN");
            Integer count = (Integer) body.get("count");
            if (id == null) {
                if (name == null || nameCN == null)
                    throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
                adminService.createTag(name, nameCN);
            } else {
                adminService.updateTag(id.longValue(), name, nameCN, count);
            }
            return ResponseEntity.ok().body(new AdminResponse(true, "", null, null, null, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tags")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteTag(@RequestBody Map<String, Object> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_CREATE_TAG", HttpStatus.BAD_REQUEST);
            Number id = (Number) body.get("id");
            if (id == null)
                throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            adminService.deleteTag(id.longValue());
            return ResponseEntity.ok().body(new AdminResponse(true, "", null, null, null, null, null, null, null));

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteAccount(@PathVariable Long userId, @RequestParam() String reason) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_DELETE_ACCOUNT", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            adminService.deleteAccount(adminUser, userId, reason);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "", null, null, null, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/chapters/{chapterId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteChapterById(@PathVariable Long chapterId, @RequestParam(required = false) String reason) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_DELETE_NOVEL", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            if (chapterId == null) throw new ApiException("UNABLE_FOUND_CHAPTER", HttpStatus.BAD_REQUEST);
            if (reason == null) reason = "";
            adminService.deleteChapterById(adminUser, chapterId, reason);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "", null, null, null, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteCommentById(@PathVariable Long commentId, @RequestParam(required = false) String reason) {
        try {
            if (commentId == null) throw new ApiException("UNABLE_FOUND_COMMENT", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_DELETE_COMMENT", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            if (reason == null) reason = "";
            adminService.deleteCommentById(adminUser, commentId, reason);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "", null, null, null, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> getUsers(@RequestParam(required = false) Long id, @RequestParam(required = false) String email, @RequestParam(required = false) String username, @RequestParam(required = false) String role, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        try {
            if (id != null) {
                UserDTO user = adminService.getUserById(id);
                return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "", user, null, null, null, null, null, null));
            } else if (email != null) {
                UserDTO user = adminService.getUserByEmail(email);
                return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "", user, null, null, null, null, null, null));
            } else {
                if (page == null) page = 0;
                if (size == null) size = 20;
                Pageable pageable = PageRequest.of(page, size);
                User.Role roleEnum = User.Role.getRole(role);
                Page<UserDTO> users;
                if (username == null) {
                    if (roleEnum == null) {
                        users = adminService.getAll(pageable);
                    } else {
                        users = adminService.getAllByRole(roleEnum, pageable);
                    }
                } else {
                    if (roleEnum == null) {
                        users = adminService.searchByUsername(username, pageable);
                    } else {
                        users = adminService.searchByUsernameRole(username, roleEnum, pageable);
                    }
                }
                return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "", null, new PageDTO<>(users), null, null, null, null, null));
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> getCounts() {
        try {
            Counts counts = adminService.getCounts();
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "", null, null, counts, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/novels/{novelId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> getMyNovel(@PathVariable Long novelId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
            if (novelId == null)
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            NovelDTO novel = adminService.getNovel(novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "", null, null, null, novel, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/messages/{messageId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> readMessage(@PathVariable Long messageId) {
        try {
            if (messageId == null) throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            adminService.readMessage(messageId);
            return ResponseEntity.ok().body(new AdminResponse(true, null, null, null, null, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/messages")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> getMessages(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        try {
            if (page == null) page = 0;
            if (size == null) size = 20;
            Pageable pageable = PageRequest.of(page, size);
            Page<MessageDTO> messages = messageService.searchByRecipientId(-1L, pageable);
            return ResponseEntity.ok().body(new AdminResponse(true, null, null, null, null, null, null, new PageDTO<>(messages), null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/messages/{messageId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> getMessage(@PathVariable Long messageId) {
        try {
            if (messageId == null) throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            MessageDTO messageDTO = adminService.getMessage(messageId);
            return ResponseEntity.ok().body(new AdminResponse(true, null, null, null, null, null, messageDTO, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/messages/{messageId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
        try {
            if (messageId == null) throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            adminService.deleteMessage(messageId);
            return ResponseEntity.ok().body(new AdminResponse(true, null, null, null, null, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/messages/count")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> getUnreadCount() {
        try {
            int count = adminService.getUnreadCount();
            return ResponseEntity.ok().body(new AdminResponse(true, null, null, null, null, null, null, null, count));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/messages")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> body) {
        try {
            Number recipientId = (Number) body.get("recipientId");
            String content = (String) body.get("content");
            if (recipientId == null || content == null || content.trim().isEmpty() || recipientId.longValue() == -1)
                throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            if (content.length() > 500)
                throw new ApiException("MESSAGE_TOO_LONG", HttpStatus.BAD_REQUEST);
            messageService.createUserMessage(-1L, recipientId.longValue(), content);
            return ResponseEntity.ok().body(new AdminResponse(true, null, null, null, null, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record AdminResponse(boolean success, String message, UserDTO user, PageDTO<UserDTO> users, Counts counts,
                         NovelDTO novel, MessageDTO messageDTO, PageDTO<MessageDTO> messages, Integer unreadCount) {
    }
}
