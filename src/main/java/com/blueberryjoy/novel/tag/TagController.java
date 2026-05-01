/*
Post /: (Authority('ADMIN')) JSON -> {id, name, nameCN, count}
Get /:
*/

package com.blueberryjoy.novel.tag;

import com.blueberryjoy.novel.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createTag(@RequestBody Map<String, Object> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null || auth.getPrincipal() == null)
                throw new ApiException("Unable to create tag at this time.", HttpStatus.BAD_REQUEST);
            Integer id =  (Integer) body.get("id");
            String name = (String) body.get("name");
            String nameCN = (String) body.get("nameCN");
            Integer count = (Integer) body.get("count");
            if (id == null) {
                if (name == null || nameCN == null)
                    throw new ApiException("Please complete all required fields to proceed.", HttpStatus.BAD_REQUEST);
                TagDTO tag = tagService.createTag(name, nameCN);
                return ResponseEntity.ok().body(new TagResponse(true, "Created successfully", tag, null));
            } else {
                TagDTO tag = tagService.update(id, name, nameCN, count);
                return ResponseEntity.ok().body(new TagResponse(true, "Updated successfully", tag, null));
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getTags() {
        try {
            List<TagDTO> tags = tagService.getAll();
            return ResponseEntity.ok().body(new TagResponse(true, "Created successfully", null, tags));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record TagResponse(boolean success, String message, TagDTO tag, List<TagDTO> tags) {
    }
}
