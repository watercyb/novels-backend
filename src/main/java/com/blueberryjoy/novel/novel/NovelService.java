package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.chpater.ChapterRepository;
import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.file.FileService;
import com.blueberryjoy.novel.tag.Tag;
import com.blueberryjoy.novel.tag.TagRepository;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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
    @Autowired
    private FileService repository;

    @Transactional
    public NovelDTO getNovel(Long id) {
        Novel novel = novelRepository.findById(id).orElse(null);
        if (novel == null) return null;
        return new NovelDTO(novel);
    }

    @Transactional
    public NovelDTO createNovel(String title, String description, User user, MultipartFile coverImage) {
        Novel novel = new Novel();
        novel.setTitle(title);
        novel.setDescription(description);
        novel.setAuthor(user);
        novel.setCoverUrl("");
        novel.setLastChapterTitle("");
        novel = novelRepository.save(novel);

        if (coverImage != null && !coverImage.isEmpty()) {
            String contentType = coverImage.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                if (coverImage.getSize() > 5 * 1024 * 1024)
                    throw new ApiException("This file is larger than 5MB. Please upload a smaller version.", HttpStatus.BAD_REQUEST);
                String fileName = repository.uploadNovelCover(coverImage, novel.getId());
                if (fileName == null || fileName.isEmpty())
                    throw new ApiException("Unable to update user avatar at this time.", HttpStatus.BAD_REQUEST);
                novel.setCoverUrl(fileName);
            }
        }
        Chapter chapter = new Chapter();
        chapter.setTitle("");
        chapter.setNovel(novel);
        chapter.setOrderIndex(BigDecimal.ZERO);
        chapter = chapterRepository.save(chapter);

        novel.setLastUpdatedAt(chapter.getUpdatedAt());
        novel = novelRepository.save(novel);
        user.updateNovelCount(1);
        userRepository.save(user);

        return new NovelDTO(novel);
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
    public void addTag(User user, long novelId, long tagId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (novel == null || tag == null)
            throw new ApiException("Invalid input.", HttpStatus.BAD_REQUEST);
        if (!Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("You do not have permission to edit this novel.", HttpStatus.BAD_REQUEST);
        if (!novel.getTags().contains(tag)) {
            tag.updateCount(1);
            tagRepository.save(tag);
            novel.addTag(tag);
        }
    }

    @Transactional
    public void removeTag(User user, long novelId, long tagId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (novel == null || tag == null)
            throw new ApiException("Invalid input.", HttpStatus.BAD_REQUEST);
        if (!Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("You do not have permission to edit this novel.", HttpStatus.BAD_REQUEST);
        if (novel.getTags().contains(tag)) {
            tag.updateCount(-1);
            tagRepository.save(tag);
            novel.removeTag(tag);
        }
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

    @Transactional
    public Novel save(Novel novel) {
        return novelRepository.save(novel);
    }

    @Transactional
    public void deleteById(User user, long novelId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
        if (!Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("You do not have permission to delete this novel.", HttpStatus.BAD_REQUEST);

        List<Long> tagIds = novel.getTags()
                .stream()
                .map(Tag::getId)
                .toList();
        tagRepository.decreaseCount(tagIds);

        repository.deleteNovelCover(novel);
        novel.getTags().clear();
        novelRepository.save(novel);
        chapterRepository.deleteByNovelId(novel.getId());
        novelRepository.delete(novel);
        user.updateNovelCount(-1);
        userRepository.save(user);
    }
}
