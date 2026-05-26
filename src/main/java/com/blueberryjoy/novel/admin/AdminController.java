/*
Delete /{novelId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: novelId
Post /user/{userId} (hasAnyAuthority('ADMIN', 'SUPERADMIN')) PathVariable: userId, JSON: {role} -> UserDTO
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
                throw new ApiException("Unable to delete novel right now.", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            if (novelId == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
            adminService.deleteById(adminUser, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "Novel deleted"));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        try {
            String roleStr = (String) body.get("role");
            User.Role newRole = User.Role.getRole(roleStr);
            if (newRole == null)
                throw new ApiException("Please complete all required fields to proceed.", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to update user role right now.", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            adminService.updateUserRole(adminUser, userId, newRole);
            return ResponseEntity.status(HttpStatus.OK).body(new AdminResponse(true, "User role updated"));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record AdminResponse(boolean success, String message) {
    }
}
