package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.chpater.ChapterRepository;
import com.blueberryjoy.novel.tag.Tag;
import com.blueberryjoy.novel.tag.TagRepository;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class NovelService {

    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public NovelDTO getNovel(Long id) {
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
    public List<NovelDTO> getNovels(long UserId) {
        List<Novel> list = novelRepository.findNovels(UserId);
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
        if (novel.getTags().contains(tag))
            return true;
        tag.updateCount(1);
        tagRepository.save(tag);
        novel.addTag(tag);
        return true;
    }

    @Transactional
    public boolean removeTag(long novelId, long tagId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (novel == null || tag == null) return false;
        if (!novel.getTags().contains(tag))
            return true;
        tag.updateCount(-1);
        tagRepository.save(tag);
        novel.removeTag(tag);
        return true;
    }

    @Transactional
    public boolean deleteNovel(long novelId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null) return false;

        List<Long> tagIds = novel.getTags()
                .stream()
                .map(Tag::getId)
                .toList();
        tagRepository.decreaseCount(tagIds);

        novel.getTags().clear();
        novelRepository.save(novel);
        chapterRepository.deleteByNovelId(novel.getId());
        novelRepository.delete(novel);
        return true;
    }

    @Transactional
    public Page<NovelDTO> searchByKeywordAndTag(String keyword, List<String> tags, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Novel> novels = novelRepository.searchByKeywordAndTag(keyword, tags, pageable);
        return novels.map(NovelDTO::new);
    }

    @Transactional
    public Page<NovelDTO> searchByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Novel> novels = novelRepository.searchByKeyword(keyword, pageable);
        return novels.map(NovelDTO::new);
    }

    @Transactional
    public Page<NovelDTO> searchByTags(List<String> tags, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Novel> novels = novelRepository.searchByTags(tags, pageable);
        return novels.map(NovelDTO::new);
    }
}
