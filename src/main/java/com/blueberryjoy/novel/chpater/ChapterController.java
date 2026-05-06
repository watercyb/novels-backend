/*
Get /{chapterId} PathVariable: chapterId -> List<ChapterDTO> (chapter, prv, next)
Get / RequestParam: novelId(long),  pageable(Pageable) -> PageDTO<ChapterDTO>
Post / (authenticated) JSON: {novelId, chapterId, title, content} -> List<ChapterDTO>
Delete /{novelId} (authenticated) PathVariable: novelId
Post /move (authenticated) JSON: {chapterId, targetId} -> List<ChapterDTO>
*/


package com.blueberryjoy.novel.chpater;

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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {
    @Autowired
    private ChapterService chapterService;

    @GetMapping("/{chapterId}")
    public ResponseEntity<?> getChapterById(@PathVariable Long chapterId) {
        try {
            if (chapterId == null) throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
            List<ChapterDTO> list = chapterService.getChapter(chapterId);
            return ResponseEntity.status(HttpStatus.OK).body(new ChapterResponse(true, "", list, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getChaptersByNovelId(@RequestParam(required = false) Long novelId, Pageable pageable) {
        try {
            if (novelId == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            Page<ChapterDTO> chapters = chapterService.getChapters(novelId, pageable);
            return ResponseEntity.status(HttpStatus.OK).body(new ChapterResponse(true, "", null, new PageDTO<>(chapters)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrUpdateChapter(@RequestBody Map<String, Object> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to update chapter right now.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            Number novelId = (Number) body.get("novelId");
            Number chapterId = (Number) body.get("chapterId");
            String title = (String) body.get("title");
            String content = (String) body.get("content");
            if (novelId == null || title == null || content == null)
                throw new ApiException("Please complete all required fields to proceed.", HttpStatus.BAD_REQUEST);
            if (chapterId == null) {
                ChapterDTO chapter = chapterService.createChapter(user, novelId.longValue(), title, content);
                return ResponseEntity.status(HttpStatus.OK).body(new ChapterResponse(true, "Chapter created successfully", List.of(chapter), null));
            } else {
                ChapterDTO chapter = chapterService.updateChapter(user, novelId.longValue(), chapterId.longValue(), title, content);
                return ResponseEntity.status(HttpStatus.OK).body(new ChapterResponse(true, "Chapter updated successfully", List.of(chapter), null));
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{chapterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteChapterById(@PathVariable Long chapterId) {
        try {
            if (chapterId == null) throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to delete chapter right now.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            chapterService.deleteChapter(user, chapterId);
            return ResponseEntity.status(HttpStatus.OK).body(new ChapterResponse(true, "Chapter deleted successfully", null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/move")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> move(@RequestBody Map<String, Object> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to move chapter right now.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            Number chapterId = (Number) body.get("chapterId");
            Number targetId = (Number) body.get("targetId");
            if (chapterId == null || targetId == null)
                throw new ApiException("This action isn’t allowed.", HttpStatus.BAD_REQUEST);
            ChapterDTO chapter = chapterService.move(user, chapterId.longValue(), targetId.longValue());
            return ResponseEntity.status(HttpStatus.OK).body(new ChapterResponse(true, "Chapter moved successfully", List.of(chapter), null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record ChapterResponse(boolean success, String message, List<ChapterDTO> chapter, PageDTO<ChapterDTO> page) {
    }
}
