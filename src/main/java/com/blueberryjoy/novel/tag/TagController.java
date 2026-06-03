/*
Get /
*/

package com.blueberryjoy.novel.tag;

import com.blueberryjoy.novel.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping
    public ResponseEntity<?> getTags() {
        try {
            List<TagDTO> tags = tagService.getAll();
            return ResponseEntity.ok().body(new TagResponse(true, "Created successfully", null, tags));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    record TagResponse(boolean success, String message, TagDTO tag, List<TagDTO> tags) {
    }
}
