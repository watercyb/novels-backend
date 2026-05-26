/*
Delete /{novelId} (hasAuthority('ADMIN', 'SUPERADMIN')) PathVariable: novelId
*/


package com.blueberryjoy.novel.admin;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.novel.NovelController;
import com.blueberryjoy.novel.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @DeleteMapping("/novels/{novelId}")
    @PreAuthorize("hasAuthority('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> deleteNovelById(@PathVariable Long novelId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to delete novel right now.", HttpStatus.BAD_REQUEST);
            User adminUser = (User) auth.getPrincipal();
            if (novelId == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
            adminService.deleteById(adminUser, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "Novel deleted", null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
