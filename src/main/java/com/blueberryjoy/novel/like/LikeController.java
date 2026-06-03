/*
Put /{novelId} (authenticated) PathVariable: novelId
Delete /{novelId} (authenticated) PathVariable: novelId
Get / (authenticated)
*/

package com.blueberryjoy.novel.like;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.favorite.FavoriteService;
import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelDTO;
import com.blueberryjoy.novel.novel.NovelRepository;
import com.blueberryjoy.novel.novel.PageDTO;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserRepository;
import com.blueberryjoy.novel.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PutMapping("/{novelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> like(@PathVariable Long novelId) {
        try {
            if (novelId == null) throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_LIKE_NOVEL", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            likeService.like(user, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new LikeResponse(true, "Liked successfully", null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{novelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> unlike(@PathVariable Long novelId) {
        try {
            if (novelId == null)
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_UNLIKE_NOVEL", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            likeService.unlike(user, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new LikeResponse(true, "Unliked successfully", null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getLikes() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_LOAD_LIKES", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            List<Long> list = likeService.getIds(user);
            return ResponseEntity.status(HttpStatus.OK).body(new LikeResponse(true, "", list));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record LikeResponse(boolean success, String message, List<Long> list) {
    }
}