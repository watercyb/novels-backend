package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.chpater.ChapterRepository;
import com.blueberryjoy.novel.chpater.ChapterService;
import com.blueberryjoy.novel.tag.Tag;
import com.blueberryjoy.novel.tag.TagRepository;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserRepository;
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
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;

    public NovelService(NovelRepository novelRepository, TagRepository tagRepository, ChapterRepository chapterRepository, UserRepository userRepository) {
        this.novelRepository = novelRepository;
        this.tagRepository = tagRepository;
        this.chapterRepository = chapterRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public NovelDTO getNovelDTO(Long id) {
        Novel novel = novelRepository.findById(id).orElse(null);
        if (novel == null) return null;
        return new NovelDTO(novel);
    }

    @Transactional
    public Novel createNovel(String title, String description, long userId, String coverUrl) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        Novel novel = new Novel();
        novel.setTitle(title);
        novel.setDescription(description);
        novel.setAuthor(user);
        novel.setCoverUrl(coverUrl);

        novel.setLastChapterTitle("");
        novel = novelRepository.save(novel);

        Chapter chapter = new Chapter();
        chapter.setTitle("");
        chapter.setNovel(novel);
        chapter.setOrderIndex(BigDecimal.ZERO);
        chapterRepository.save(chapter);

        novel.setLastUpdatedAt(chapter.getUpdatedAt());
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

    @Transactional
    public boolean deleteNovel(long novelId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null) return false;
        novel.getTags().clear();
        novelRepository.save(novel);
        chapterRepository.deleteByNovelId(novel.getId());
        novelRepository.delete(novel);
        return true;
    }
}
