/*
Get / RequestParam: chapterId(long), parentId(long), commentId(long), content(String) -> PageDTO<CommentDTO>
Post / (authenticated) JSON: {novelId, chapterId, title, content} -> ChapterDTO
Delete /{commentId} (authenticated) PathVariable: commentId
*/


package com.blueberryjoy.novel.comment;

import com.blueberryjoy.novel.chpater.ChapterController;
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

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<?> getComments(@RequestParam(required = false) Long chapterId, @RequestParam(required = false) Long commentId, @RequestParam() Pageable pageable) {
        try {
            if (chapterId == null && commentId == null)
                throw new ApiException("Invalid input", HttpStatus.BAD_REQUEST);
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            Page<CommentDTO> comments;
            if (commentId == null) {
                comments = commentService.getCommentsByChapter(chapterId, pageable);
            } else {
                comments = commentService.getCommentsByComment(commentId, pageable);
            }
            return ResponseEntity.status(HttpStatus.OK).body(new CommentResponse(true, "", null, new PageDTO<>(comments)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrUpdateComment(@RequestBody Map<String, Object> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to create/update comment at this time.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            Number chapterId = (Number) body.get("chapterId");
            Number parentId = (Number) body.get("parentId");
            Number commentId = (Number) body.get("commentId");
            String content = (String) body.get("content");
            if ((chapterId == null && parentId == null && commentId == null) || content == null)
                throw new ApiException("Please complete all required fields to proceed.", HttpStatus.BAD_REQUEST);
            if (commentId == null) {
                CommentDTO comment = commentService.createComment(user, chapterId, parentId, content);
                return ResponseEntity.status(HttpStatus.OK).body(new CommentResponse(true, "Comment created successfully", comment, null));
            } else {
                CommentDTO comment = commentService.updateComment(user, commentId, content);
                return ResponseEntity.status(HttpStatus.OK).body(new CommentResponse(true, "Comment updated successfully", comment, null));
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteCommentById(@PathVariable Long commentId) {
        try {
            if (commentId == null) throw new ApiException("Comment not found", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to delete comment at this time.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            commentService.deleteComment(user, commentId);
            return ResponseEntity.status(HttpStatus.OK).body(new CommentResponse(true, "Comment deleted successfully", null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record CommentResponse(boolean success, String message, CommentDTO comment, PageDTO<CommentDTO> page) {
    }
}