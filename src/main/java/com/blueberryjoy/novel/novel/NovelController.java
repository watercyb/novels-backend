/*
Get /{novelId} PathVariable: novelId -> NovelDTO
Get / RequestParam: userId(long), title(String), tagIds(List<Long>), pageable(Pageable), type(String), tags(List<Long>) -> PageDTO<NovelDTO>
Post / (hasAnyAuthority('AUTHOR', 'ADMIN', 'SUPERADMIN')) form-data: coverImage(file), id(long), title(String), description(String), type(String), tags(List<Long>) -> NovelDTO
Delete /{novelId} (authenticated) PathVariable: novelId
Put /{novelId}/tags/{tagId} (authenticated) PathVariable: novelId, tagId
Delete /{novelId}/tags/{tagId} (authenticated) PathVariable: novelId, tagId
Get /my-novels (authenticated) -> List<NovelDTO>
Get /my-novels/{novelId} (authenticated) PathVariable: novelId -> NovelDTO
*/

package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.homepage.TopTenService;
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
    @Autowired
    private TopTenService topTenService;

    private static final Set<String> allowedFields = Set.of("title", "like_count", "chapter_count", "word_count", "last_updated_at");

    @GetMapping("/{novelId}")
    public ResponseEntity<?> getNovelById(@PathVariable Long novelId) {
        try {
            if (novelId == null) throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
            NovelDTO novel = novelService.getNovel(novelId);
            topTenService.increaseTodayCount(novel.getId());
            return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", novel, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getNovelsByUserId(@RequestParam(required = false) Long userId, @RequestParam(required = false) String title, @RequestParam(required = false) List<Long> tagIds, Pageable pageable, @RequestParam(required = false) String type) {
        try {
            pageable = filterPageable(pageable);
            if (userId == null && title == null && tagIds == null) {
                Page<NovelDTO> novels = novelService.getAllNovels(pageable, type);
                return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", null, new PageDTO<>(novels), null));
            } else if (userId != null) {
                Page<NovelDTO> novels = novelService.getNovelsByUserId(userId, pageable, type);
                return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", null, new PageDTO<>(novels), null));
            } else if (title == null) {
                Page<NovelDTO> novels = novelService.searchByTags(tagIds, pageable, type);
                return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", null, new PageDTO<>(novels), null));
            } else if (tagIds == null) {
                Page<NovelDTO> novels = novelService.searchByKeyword(title, pageable, type);
                return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", null, new PageDTO<>(novels), null));
            } else {
                Page<NovelDTO> novels = novelService.searchByKeywordAndTag(title, tagIds, pageable, type);
                return ResponseEntity.status(HttpStatus.OK).body(new NovelController.NovelResponse(true, "", null, new PageDTO<>(novels), null));
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('AUTHOR', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> createOrUpdateNovel(@RequestParam(value = "coverImage", required = false) MultipartFile coverImage, @RequestParam(value = "id", required = false) Long id, @RequestParam(value = "title", required = false) String title, @RequestParam(value = "description", required = false) String description, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "tags", required = false) List<Long> tags) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_CREATE_NOVEL", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (id != null) {
                NovelDTO novel = novelService.updateNovel(coverImage, user, id, title, description, type, tags);
                return ResponseEntity.ok().body(new NovelController.NovelResponse(true, null, novel, null, null));
            } else {
                if (title == null) throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
                if (description == null) description = "";
                NovelDTO novel = novelService.createNovel(coverImage, user, title, description, type, tags);
                return ResponseEntity.ok().body(new NovelController.NovelResponse(true, null, novel, null, null));
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{novelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteNovelById(@PathVariable Long novelId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_DELETE_NOVEL", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (novelId == null) throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
            novelService.deleteById(user, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{novelId}/tags/{tagId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addTag(@PathVariable Long novelId, @PathVariable Long tagId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_ADD_TAG", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (novelId == null || tagId == null)
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            novelService.addTag(user, novelId, tagId);
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{novelId}/tags/{tagId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeTag(@PathVariable Long novelId, @PathVariable Long tagId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_REMOVE_TAG", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (novelId == null || tagId == null)
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            novelService.removeTag(user, novelId, tagId);
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, null, null, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my-novels")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyNovels() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            List<NovelDTO> novels = novelService.getMyNovels(user.getId());
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, "", null, null, novels));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my-novels/{novelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyNovel(@PathVariable Long novelId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null)
                throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
            User user = (User) auth.getPrincipal();
            if (novelId == null)
                throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
            NovelDTO novel = novelService.getMyNovel(user, novelId);
            return ResponseEntity.status(HttpStatus.OK).body(new NovelResponse(true, "", novel, null, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
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

    public record NovelResponse(boolean success, String message, NovelDTO novel, PageDTO<NovelDTO> page,
                                List<NovelDTO> novels) {
    }
}