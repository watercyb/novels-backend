package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.chpater.ChapterService;
import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.repository.Repository;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/novels")
public class NovelController {
    @Autowired
    private NovelService novelService;
    @Value("${REPO_URL}")
    private String repoUrl;
    @Autowired
    private UserService userService;
    @Autowired
    private Repository repository;
    @Autowired
    private ChapterService chapterService;

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
            String coverUrl = "";

            Novel novel = novelService.createNovel(title, description, user, coverUrl);

            if (coverImage != null && !coverImage.isEmpty()) {
                String contentType = coverImage.getContentType();
                if (contentType != null && contentType.startsWith("image/")) {
                    if (coverImage.getSize() > 5 * 1024 * 1024)
                        throw new ApiException("This file is larger than 5MB. Please upload a smaller version.", HttpStatus.BAD_REQUEST);
                    String fileName = repository.uploadNovelCover(coverImage, novel.getId());
                    if (fileName == null || fileName.isEmpty())
                        throw new ApiException("Unable to update user avatar at this time.", HttpStatus.BAD_REQUEST);
                    novel.setCoverUrl(fileName);
                }
            }

            Chapter chapter = chapterService.createChapter("", novel, null, null, BigDecimal.ZERO);
            novel.setLastUpdatedAt(chapter.getUpdatedAt());
            novel = novelService.save(novel);
            userService.updateNovels(user, 1);

            return ResponseEntity.ok().body(new NovelController.NovelResponse(true, "Created successfully", new NovelDTO(novel)));
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