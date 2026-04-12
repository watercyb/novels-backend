package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.chpater.ChapterService;
import com.blueberryjoy.novel.user.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class NovelService {

    private final NovelRepository novelRepository;

    public NovelService(NovelRepository novelRepository) {
        this.novelRepository = novelRepository;
    }

    public Novel getNovel(Long id) {
        return novelRepository.findById(id).orElse(null);
    }

    public Novel createNovel(String title, String description, User user, String coverUrl) {
        Novel novel = new Novel();
        novel.setTitle(title);
        novel.setDescription(title);
        novel.setAuthor(user);
        novel.setCoverUrl(coverUrl);
        return novelRepository.save(novel);
    }

    public List<NovelPreviewDTO> getNovels(User user) {
        return novelRepository.findNovelPreview(user.getId());
    }
}
