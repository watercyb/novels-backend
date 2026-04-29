package com.blueberryjoy.novel.favorite;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.novel.NovelController;
import com.blueberryjoy.novel.novel.NovelDTO;
import com.blueberryjoy.novel.novel.PageDTO;
import com.blueberryjoy.novel.user.User;
import org.springframework.beans.factory.annotation.Autowired;
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
            if (novelId == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to add tag at this time.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            favoriteService.addFavorite(user, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new FavoriteResponse(true, "Favorite successfully", null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{favoriteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeFavorite(@PathVariable Long favoriteId) {
        try {
            if (favoriteId == null) throw new ApiException("Unable to remove from favorites at the moment.", HttpStatus.BAD_REQUEST);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to remove from favorites at the moment.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            favoriteService.removeFavorite(user, favoriteId);
            return ResponseEntity.status(HttpStatus.OK).body(new FavoriteResponse(true, "Removed from favorites successfully", null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record FavoriteResponse(boolean success, String message, List<Long> list, PageDTO<NovelDTO> page) {
    }
}