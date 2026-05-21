package com.blueberryjoy.novel.novel;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.chpater.ChapterRepository;
import com.blueberryjoy.novel.comment.CommentRepository;
import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.file.FileService;
import com.blueberryjoy.novel.tag.Tag;
import com.blueberryjoy.novel.tag.TagRepository;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public NovelDTO getNovel(Long id) {
        Novel novel = novelRepository.findById(id).orElse(null);
        if (novel == null) return null;
        return new NovelDTO(novel);
    }

    @Transactional
    public NovelDTO createNovel(MultipartFile coverImage, User user, String title, String description, String type) {
        Novel novel = new Novel();
        novel.setTitle(title);
        novel.setDescription(description);
        Novel.Type novelType = Novel.Type.getType(type);
        if (novelType == null) throw new ApiException("Novel type cannot be empty.", HttpStatus.BAD_REQUEST);
        novel.setType(novelType);
        novel.setAuthor(user);
        novel.setCoverUrl("");
        novel.setLastChapterTitle("");
        novel = novelRepository.save(novel);

        if (coverImage != null && !coverImage.isEmpty()) {
            String contentType = coverImage.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                if (coverImage.getSize() > 5 * 1024 * 1024)
                    throw new ApiException("This file is larger than 5MB. Please upload a smaller version.", HttpStatus.BAD_REQUEST);
                String fileName = repository.uploadNovelCover(coverImage, user.getId(), novel.getId(), novel.getCoverUrl());
                if (fileName == null || fileName.isEmpty())
                    throw new ApiException("Unable to update cover image right now.", HttpStatus.BAD_REQUEST);
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
    public void addTag(User user, long novelId, long tagId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (novel == null || tag == null) throw new ApiException("This action isn’t allowed.", HttpStatus.BAD_REQUEST);
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
        if (novel == null || tag == null) throw new ApiException("This action isn’t allowed.", HttpStatus.BAD_REQUEST);
        if (!Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("You do not have permission to edit this novel.", HttpStatus.BAD_REQUEST);
        if (novel.getTags().contains(tag)) {
            tag.updateCount(-1);
            tagRepository.save(tag);
            novel.removeTag(tag);
        }
    }

    @Transactional
    public Page<NovelDTO> searchByKeywordAndTag(String keyword, List<Long> tags, Pageable pageable, String type) {
        Page<Novel> novels;
        Novel.Type novelType = Novel.Type.getType(type);
        if (pageable.getSort().isSorted()) {
            if (novelType == null) {
                novels = novelRepository.searchByKeywordAndTagSort(keyword, tags, pageable);
            } else {
                novels = novelRepository.searchByKeywordTagAndTypeSort(keyword, tags, novelType.name(), pageable);
            }
        } else {
            if (novelType == null) {
                novels = novelRepository.searchByKeywordAndTag(keyword, tags, pageable);
            } else {
                novels = novelRepository.searchByKeywordTagAndType(keyword, tags, novelType.name(), pageable);
            }
        }
        return novels.map(NovelDTO::new);
    }

    @Transactional
    public Page<NovelDTO> searchByKeyword(String keyword, Pageable pageable, String type) {
        Page<Novel> novels;
        Novel.Type novelType = Novel.Type.getType(type);
        if (pageable.getSort().isSorted()) {
            if (novelType == null) {
                novels = novelRepository.searchByKeywordSort(keyword, pageable);
            } else {
                novels = novelRepository.searchByKeywordAndTypeSort(keyword, novelType.name(), pageable);
            }
        } else {
            if (novelType == null) {
                novels = novelRepository.searchByKeyword(keyword, pageable);
            } else {
                novels = novelRepository.searchByKeywordAndType(keyword, novelType.name(), pageable);
            }
        }
        return novels.map(NovelDTO::new);
    }

    @Transactional
    public Page<NovelDTO> searchByTags(List<Long> tags, Pageable pageable, String type) {
        Page<Novel> novels;
        Novel.Type novelType = Novel.Type.getType(type);
        if (novelType == null) {
            novels = novelRepository.searchByTags(tags, pageable);
        } else {
            novels = novelRepository.searchByTagsAndType(tags, novelType.name(), pageable);
        }
        return novels.map(NovelDTO::new);
    }

    @Transactional
    public Page<NovelDTO> getNovelsByUserId(long userId, Pageable pageable, String type) {
        Page<Novel> novels;
        Novel.Type novelType = Novel.Type.getType(type);
        if (novelType == null) {
            novels = novelRepository.searchByUseId(userId, pageable);
        } else {
            novels = novelRepository.searchByUseIdAndType(userId, novelType.name(), pageable);
        }
        return novels.map(NovelDTO::new);
    }

    @Transactional
    public Page<NovelDTO> getAllNovels(Pageable pageable, String type) {
        Page<Novel> novels;
        Novel.Type novelType = Novel.Type.getType(type);
        if (novelType == null) {
            novels = novelRepository.queryAllNovels(pageable);
        } else {
            novels = novelRepository.queryAllNovelsByType(novelType.name(), pageable);
        }
        return novels.map(NovelDTO::new);
    }

    @Transactional
    public void deleteById(User user, long novelId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
        if (!Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("You do not have permission to delete this novel.", HttpStatus.BAD_REQUEST);

        List<Long> tagIds = novel.getTags().stream().map(Tag::getId).toList();
        tagRepository.decreaseCount(tagIds);

        int chapterCount = novel.getChapterCount();
        int wordCount = novel.getWordCount();
        commentRepository.deleteRepliesByNovel(novel.getId());
        commentRepository.deleteCommentsByNovel(novel.getId());
        chapterRepository.deleteChaptersByNovel(novel.getId());
        repository.deleteNovelCover(novel, user.getId());
        novel.getTags().clear();
        novelRepository.save(novel);
        chapterRepository.deleteByNovelId(novel.getId());
        novelRepository.delete(novel);
        user.updateNovelCount(-1);
        user.updateChapterCount(-chapterCount);
        user.updateWordCount(-wordCount);
        userRepository.save(user);
    }

    @Transactional
    public NovelDTO updateNovel(MultipartFile coverImage, User user, long id, String title, String description, String type) {
        Novel novel = novelRepository.findById(id).orElse(null);
        if (novel == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
        if (!Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("You do not have permission to update this novel.", HttpStatus.BAD_REQUEST);
        if (title != null) novel.setTitle(title);
        Novel.Type novelType = Novel.Type.getType(type);
        if (novelType != null)
            novel.setType(novelType);
        if (description != null) novel.setDescription(description);
        if (coverImage != null && !coverImage.isEmpty()) {
            String contentType = coverImage.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                if (coverImage.getSize() > 5 * 1024 * 1024)
                    throw new ApiException("This file is larger than 5MB. Please upload a smaller version.", HttpStatus.BAD_REQUEST);
                String fileName = repository.uploadNovelCover(coverImage, user.getId(), novel.getId(), novel.getCoverUrl());
                if (fileName == null || fileName.isEmpty())
                    throw new ApiException("Unable to update cover image right now.", HttpStatus.BAD_REQUEST);
                novel.setCoverUrl(fileName);
            }
        }
        novel = novelRepository.save(novel);
        return new NovelDTO(novel);
    }
}
