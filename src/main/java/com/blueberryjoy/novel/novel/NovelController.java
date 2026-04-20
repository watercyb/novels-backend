package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserController;
import com.blueberryjoy.novel.user.UserDTO;
import com.blueberryjoy.novel.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/novels")
public class NovelController {
    @Autowired
    private NovelService novelService;
    @Value("${REPO_URL}")
    private String repoUrl;
    @Autowired
    private UserService userService;

    @GetMapping("/novels/{id}")
    public ResponseEntity<?> getNovelById(@PathVariable Long novelId) {
        try {
            if (novelId == null)
                throw new ApiException("Please complete all required fields to proceed.", HttpStatus.BAD_REQUEST);
            NovelDTO novel = novelService.getNovel(novelId);
            return ResponseEntity.status(HttpStatus.OK).body(novel);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/novels")
    public ResponseEntity<?> getNovelsByUserId(@RequestParam(required = false) Long userId) {
        try {
            if (userId == null)
                throw new ApiException("Please complete all required fields to proceed.", HttpStatus.BAD_REQUEST);
            List<NovelDTO> novels = novelService.getNovels(userId);
            return ResponseEntity.status(HttpStatus.OK).body(novels);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}