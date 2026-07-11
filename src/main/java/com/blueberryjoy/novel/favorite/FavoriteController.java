/*
Put /{novelId} (authenticated) PathVariable: novelId
Delete /{favoriteId} (authenticated) PathVariable: favoriteId
Get /: (authenticated) RequestParam: pageable(Pageable)
Get /ids: (authenticated) RequestParam: pageable(Pageable)
*/

package com.blueberryjoy.novel.favorite;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.novel.NovelDTO;
import com.blueberryjoy.novel.novel.PageDTO;
import com.blueberryjoy.novel.user.User;
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
@RequestMapping("/api/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @PutMapping("/{novelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addFavorite(@PathVariable Long novelId) {
        try {
            if (novelId == null) throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_ADD_FAVORITES", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            favoriteService.addFavorite(user, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new FavoriteResponse(true, "Favorite successfully", null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{novelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteFavorite(@PathVariable Long novelId) {
        try {
            if (novelId == null)
                throw new ApiException("UNABLE_REMOVE_FAVORITES", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_REMOVE_FAVORITES", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            favoriteService.deleteFavorite(user, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new FavoriteResponse(true, "Removed from favorites successfully", null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFavorites(Pageable pageable) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_GET_FAVORITES", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            Page<NovelDTO> novels = favoriteService.getNovels(user, pageable);
            return ResponseEntity.status(HttpStatus.OK).body(new FavoriteResponse(true, "", null, new PageDTO<>(novels)));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ids")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFavoriteIds() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_GET_FAVORITES", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            List<Long> list = favoriteService.getIds(user);
            return ResponseEntity.status(HttpStatus.OK).body(new FavoriteResponse(true, "", list, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record FavoriteResponse(boolean success, String message, List<Long> list, PageDTO<NovelDTO> page) {
    }
}