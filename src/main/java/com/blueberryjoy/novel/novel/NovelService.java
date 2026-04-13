package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.chpater.ChapterService;
import com.blueberryjoy.novel.tag.Tag;
import com.blueberryjoy.novel.tag.TagRepository;
import com.blueberryjoy.novel.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class NovelService {

    private final NovelRepository novelRepository;
    private final TagRepository tagRepository;

    public NovelService(NovelRepository novelRepository, TagRepository tagRepository) {
        this.novelRepository = novelRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public NovelDTO getNovelDTO(Long id) {
        Novel novel = novelRepository.findById(id).orElse(null);
        if (novel == null) return null;
        return new NovelDTO(novel);
    }

    public Novel createNovel(String title, String description, User user, String coverUrl) {
        Novel novel = new Novel();
        novel.setTitle(title);
        novel.setDescription(title);
        novel.setAuthor(user);
        novel.setCoverUrl(coverUrl);
        return novelRepository.save(novel);
    }

    @Transactional
    public List<NovelDTO> getNovels(User user) {
        List<Novel> list = novelRepository.findNovels(user.getId());
        List<NovelDTO> novelDTOs = new ArrayList<>();
        for (Novel novel : list) {
            novelDTOs.add(new NovelDTO(novel));
        }
        return novelDTOs;
    }

    @Transactional
    public boolean addTag(long novelId, long tagId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (novel == null || tag == null) return false;
        novel.addTag(tag);
        return true;
    }

    @Transactional
    public boolean removeTag(long novelId, long tagId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (novel == null || tag == null) return false;
        novel.removeTag(tag);
        return true;
    }
}
