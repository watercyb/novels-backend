/*
Delete /novels/{novelId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: novelId
Post /user/{userId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: userId, JSON: {role}
Post /tags (hasAnyAuthority('ADMIN', 'SUPERADMIN')) JSON: {id, name, nameCN, count}
Delete /tags (hasAnyAuthority('ADMIN', 'SUPERADMIN')) JSON: {id}
Delete /user/{userId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: userId
Delete /chapters/{chapterId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: chapterId
Delete /comments/{commentId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: commentId
*/


package com.blueberryjoy.novel.admin;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.user.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @DeleteMapping("/novels/{novelId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteNovelById(@PathVariable Long novelId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_DELETE_NOVEL", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            if (novelId == null) throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
            adminService.deleteNovelById(adminUser, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, ""));
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
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, ""));
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
            return ResponseEntity.ok().body(new AdminResponse(true, ""));
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
            return ResponseEntity.ok().body(new AdminResponse(true, ""));

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteAccount(@PathVariable Long userId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_DELETE_ACCOUNT", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            adminService.deleteAccount(adminUser, userId);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, ""));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/chapters/{chapterId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteChapterById(@PathVariable Long chapterId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_DELETE_NOVEL", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            if (chapterId == null) throw new ApiException("UNABLE_FOUND_CHAPTER", HttpStatus.BAD_REQUEST);
            adminService.deleteChapterById(adminUser, chapterId);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, ""));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteCommentById(@PathVariable Long commentId) {
        try {
            if (commentId == null) throw new ApiException("UNABLE_FOUND_COMMENT", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_DELETE_COMMENT", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            adminService.deleteCommentById(adminUser, commentId);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, ""));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record AdminResponse(boolean success, String message) {
    }
}
