/*
Get / RequestParam: chapterId(long), parentId(long), commentId(long), content(String) -> PageDTO<CommentDTO>
Post / (authenticated) JSON: {chapterId, parentId, commentId, content} -> ChapterDTO
Delete /{commentId} (authenticated) PathVariable: commentId
*/


package com.blueberryjoy.novel.comment;

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
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<?> getComments(@RequestParam(required = false) Long chapterId, @RequestParam(required = false) Long commentId, Pageable pageable) {
        try {
            if (chapterId == null && commentId == null)
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            if (commentId == null) {
                Page<CommentDTO> comments = commentService.getCommentsByChapter(chapterId, pageable);
                return ResponseEntity.status(HttpStatus.OK).body(new CommentResponse(true, "", null, null, new PageDTO<>(comments)));
            } else {
                List<CommentDTO> comments = commentService.getCommentsByComment(commentId);
                return ResponseEntity.status(HttpStatus.OK).body(new CommentResponse(true, "", null, comments, null));
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrUpdateComment(@RequestBody Map<String, Object> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_CREATE_OR_UPDATE_COMMENT", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            Number chapterId = (Number) body.get("chapterId");
            Number parentId = (Number) body.get("parentId");
            Number commentId = (Number) body.get("commentId");
            String content = (String) body.get("content");
            if (content == null || content.replace(" ", "").isEmpty())
                throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
            if ((chapterId == null && parentId == null && commentId == null))
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            if (commentId == null) {
                CommentDTO comment = commentService.createComment(user, chapterId, parentId, content);
                return ResponseEntity.status(HttpStatus.OK).body(new CommentResponse(true, null, comment, null, null));
            } else {
                //TODO update comment is currently not allowed.
                throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
                //CommentDTO comment = commentService.updateComment(user, commentId, content);
                //return ResponseEntity.status(HttpStatus.OK).body(new CommentResponse(true, "Comment updated successfully", comment, null));
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteCommentById(@PathVariable Long commentId) {
        try {
            if (commentId == null) throw new ApiException("UNABLE_FOUND_COMMENT", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_DELETE_COMMENT", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            commentService.deleteComment(user, commentId);
            return ResponseEntity.status(HttpStatus.OK).body(new CommentResponse(true, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record CommentResponse(boolean success, String message, CommentDTO comment, List<CommentDTO> list,
                           PageDTO<CommentDTO> page) {
    }
}