package com.blueberryjoy.novel.like;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.homepage.RecentNovelService;
import com.blueberryjoy.novel.homepage.TopTenService;
import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelRepository;
import com.blueberryjoy.novel.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private TopTenService topTenService;
    @Autowired
    private RecentNovelService recentNovelService;

    @Transactional
    public void like(User user, long novelId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null) throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
        if (likeRepository.existsByUserIdAndNovelId(user.getId(), novelId))
            throw new ApiException("ALREADY_LIKED", HttpStatus.BAD_REQUEST);
        Like like = new Like();
        like.setUser(user);
        like.setNovel(novel);
        likeRepository.save(like);
        novelRepository.updateLikeCount(novelId, 1);
        topTenService.updateTopTen(novel);
        recentNovelService.updateRecentNovel(novel);
    }

    @Transactional
    public void unlike(User user, long novelId) {
        Like like = likeRepository.findByUserIdAndNovelId(user.getId(), novelId);
        if (like == null)
            throw new ApiException("UNABLE_UNLIKE_NOVEL", HttpStatus.BAD_REQUEST);
        Novel novel = like.getNovel();
        likeRepository.delete(like);
        novelRepository.updateLikeCount(novelId, -1);
        topTenService.updateTopTen(novel);
        recentNovelService.updateRecentNovel(novel);
    }

    @Transactional
    public List<Long> getIds(User user) {
        return likeRepository.findLikedNovelIdsByUserId(user.getId());
    }
}
