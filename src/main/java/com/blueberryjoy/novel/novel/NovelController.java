package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/novels")
public class NovelController {
    @Autowired
    private NovelService novelService;

    @GetMapping("/{novelId}")
    public ResponseEntity<?> getNovelById(@PathVariable Long novelId) {
        try {
            if (novelId == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
            NovelDTO novel = novelService.getNovel(novelId);
            return ResponseEntity.status(HttpStatus.OK).body(novel);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getNovelsByUserId(@RequestParam(required = false) Long userId) {
        try {
            if (userId == null) throw new ApiException("User not found", HttpStatus.BAD_REQUEST);
            List<NovelDTO> novels = novelService.getNovels(userId);
            return ResponseEntity.status(HttpStatus.OK).body(novels);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createNovel(@RequestParam(value = "coverImage", required = false) MultipartFile coverImage, @RequestParam(value = "title") String title, @RequestParam(value = "description", required = false) String description) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to create novel at this time.", HttpStatus.BAD_REQUEST);
            if (title == null) throw new ApiException("Title cannot be empty.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (description == null) description = "";
            NovelDTO novel = novelService.createNovel(title, description, user, coverImage);
            return ResponseEntity.ok().body(new NovelController.NovelResponse(true, "Created successfully", novel));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{novelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteNovelById(@PathVariable Long novelId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to delete novel at this time.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (novelId == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
            novelService.deleteById(user, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, "Novel deleted", null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{novelId}/tags/{tagId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addTag(@PathVariable Long novelId, @PathVariable Long tagId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to add tag at this time.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (novelId == null || tagId == null) throw new ApiException("Invalid input", HttpStatus.BAD_REQUEST);
            novelService.addTag(user, novelId, tagId);
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, "Tag added", null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{novelId}/tags/{tagId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeTag(@PathVariable Long novelId, @PathVariable Long tagId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to add tag at this time.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (novelId == null || tagId == null) throw new ApiException("Invalid input", HttpStatus.BAD_REQUEST);
            novelService.removeTag(user, novelId, tagId);
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, "Tag removed", null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    record NovelResponse(boolean success, String message, NovelDTO novel) {
    }
}