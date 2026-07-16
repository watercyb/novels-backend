package com.blueberryjoy.novel.homepage;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.novel.NovelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/homepage")
public class HomepageController {
    @Autowired
    private TopTenService topTenService;

    @GetMapping("/topTen")
    public ResponseEntity<?> getTopTen() {
        try {
            List<NovelDTO> novels = topTenService.getTopTen();
            return ResponseEntity.status(HttpStatus.OK).body(new HomePageResponse(true, "", novels, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAll() {
        try {
            List<NovelDTO> novels = topTenService.getTopTen();
            return ResponseEntity.status(HttpStatus.OK).body(new HomePageResponse(true, "", novels, null));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public record HomePageResponse(boolean success, String message, List<NovelDTO> topTen, List<NovelDTO> recentTen) {
    }
}
