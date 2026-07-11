package com.blueberryjoy.novel.favorite;

import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelDTO;
import com.blueberryjoy.novel.novel.NovelRepository;
import com.blueberryjoy.novel.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private NovelRepository novelRepository;

    @Transactional
    public void addFavorite(User user, long novelId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null) throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
        if (favoriteRepository.existsByUserIdAndNovelId(user.getId(), novelId))
            throw new ApiException("ALREADY_FAVORITE", HttpStatus.BAD_REQUEST);
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setNovel(novel);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void deleteFavorite(User user, long novelId) {
        Favorite favorite = favoriteRepository.findByUserIdAndNovelId(user.getId(), novelId);
        if (favorite == null)
            throw new ApiException("UNABLE_REMOVE_FAVORITE", HttpStatus.BAD_REQUEST);
        favoriteRepository.delete(favorite);
    }

    @Transactional
    public List<Long> getIds(User user) {
        return favoriteRepository.findFavoriteNovelIdsByUserId(user.getId());
    }

    @Transactional
    public Page<NovelDTO> getNovels(User user, Pageable pageable) {
        Page<Novel> novels = favoriteRepository.findFavoriteNovelsByUserId(user.getId(), pageable);
        return novels.map(NovelDTO::new);
    }
}
