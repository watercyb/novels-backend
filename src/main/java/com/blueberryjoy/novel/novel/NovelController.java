/*
Get /{novelId} PathVariable: novelId -> NovelDTO
Get / RequestParam: userId(long), title(String), tagIds(List<Long>), pageable(Pageable) -> PageDTO<NovelDTO>
Post / (authenticated) form-data: avatarImage(file), id(long), username(String), title(String), description(String) -> NovelDTO
Delete /{novelId} (authenticated) PathVariable: novelId
Put /{novelId}/tags/{tagId} (authenticated) PathVariable: novelId, tagId
Delete /{novelId}/tags/{tagId} (authenticated) PathVariable: novelId, tagId
*/

package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/novels")
public class NovelController {
    @Autowired
    private NovelService novelService;
    private static final Set<String> allowedFields = Set.of("title", "like_count", "chapter_count", "word_count", "last_updated_at");

    @GetMapping("/{novelId}")
    public ResponseEntity<?> getNovelById(@PathVariable Long novelId) {
        try {
            if (novelId == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
            NovelDTO novel = novelService.getNovel(novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", novel, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getNovelsByUserId(@RequestParam(required = false) Long userId, @RequestParam(required = false) String title, @RequestParam(required = false) List<Long> tagIds, Pageable pageable) {
        try {
            pageable = filterPageable(pageable);
            if (userId == null && title == null && tagIds == null) {
                Page<NovelDTO> novels = novelService.getAllNovels(pageable);
                return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", null, new PageDTO<>(novels)));
            } else if (userId != null) {
                Page<NovelDTO> novels = novelService.getNovelsByUserId(userId, pageable);
                return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", null, new PageDTO<>(novels)));
            } else if (title == null) {
                Page<NovelDTO> novels = novelService.searchByTags(tagIds, pageable);
                return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", null, new PageDTO<>(novels)));
            } else if (tagIds == null) {
                Page<NovelDTO> novels = novelService.searchByKeyword(title, pageable);
                return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", null, new PageDTO<>(novels)));
            } else {
                Page<NovelDTO> novels = novelService.searchByKeywordAndTag(title, tagIds, pageable);
                return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", null, new PageDTO<>(novels)));
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrUpdateNovel(@RequestParam(value = "coverImage", required = false) MultipartFile coverImage, @RequestParam(value = "id", required = false) Long id, @RequestParam(value = "title", required = false) String title, @RequestParam(value = "description", required = false) String description) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to create novel at this time.", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (id != null) {
                NovelDTO novel = novelService.updateNovel(id, title, description, user, coverImage);
                return ResponseEntity.ok().body(new NovelController.NovelResponse(true, "Updated successfully", novel, null));
            } else {
                if (title == null) throw new ApiException("Title cannot be empty.", HttpStatus.BAD_REQUEST);
                if (description == null) description = "";
                NovelDTO novel = novelService.createNovel(title, description, user, coverImage);
                return ResponseEntity.ok().body(new NovelController.NovelResponse(true, "Created successfully", novel, null));
            }
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
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, "Novel deleted", null, null));
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
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, "Tag added", null, null));
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
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, "Tag removed", null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static Pageable filterPageable(Pageable pageable) {
        Sort sort = Sort.by(
                pageable.getSort().stream()
                        .filter(o -> allowedFields.contains(o.getProperty()))
                        .toList()
        );

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
    }

    record NovelResponse(boolean success, String message, NovelDTO novel, PageDTO<NovelDTO> page) {
    }
}