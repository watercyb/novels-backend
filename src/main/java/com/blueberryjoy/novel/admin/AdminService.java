package com.blueberryjoy.novel.admin;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.chpater.ChapterRepository;
import com.blueberryjoy.novel.comment.Comment;
import com.blueberryjoy.novel.comment.CommentRepository;
import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.favorite.FavoriteRepository;
import com.blueberryjoy.novel.file.FileService;
import com.blueberryjoy.novel.homepage.RecentNovelService;
import com.blueberryjoy.novel.homepage.TopTenService;
import com.blueberryjoy.novel.like.LikeRepository;
import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelRepository;
import com.blueberryjoy.novel.tag.Tag;
import com.blueberryjoy.novel.tag.TagRepository;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserDTO;
import com.blueberryjoy.novel.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {

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
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private TopTenService topTenService;
    @Autowired
    private RecentNovelService recentNovelService;
    @Autowired
    private FileService fileService;

    @Transactional
    public void deleteNovelById(User adminUser, long novelId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null) throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
        if (adminUser.getRole() != User.Role.ADMIN && adminUser.getRole() != User.Role.SUPERADMIN)
            throw new ApiException("NEED_PERMISSION_DELETE_NOVEL", HttpStatus.BAD_REQUEST);
        User user = novel.getAuthor();

        List<Long> tagIds = novel.getTags().stream().map(Tag::getId).toList();
        tagRepository.decreaseCount(tagIds);

        int chapterCount = novel.getChapterCount();
        int wordCount = novel.getWordCount();
        commentRepository.deleteRepliesByNovel(novel.getId());
        commentRepository.deleteCommentsByNovel(novel.getId());
        chapterRepository.deleteChaptersByNovel(novel.getId());
        likeRepository.deleteLikesByNovelId(novel.getId());
        favoriteRepository.deleteFavoritesByNovelId(novel.getId());
        repository.deleteNovelCover(novel, user.getId());
        novel.getTags().clear();
        novelRepository.save(novel);
        chapterRepository.deleteByNovelId(novel.getId());
        novelRepository.delete(novel);
        topTenService.removeFromTopTen(novel);
        recentNovelService.removeFromRecentNovels(novel);
        user.updateNovelCount(-1);
        user.updateChapterCount(-chapterCount);
        user.updateWordCount(-wordCount);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserRole(User adminUser, long userId, User.Role newRole) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) throw new ApiException("UNABLE_FOUND_USER", HttpStatus.BAD_REQUEST);
        if (user.getRole() == User.Role.SUPERADMIN || newRole == User.Role.SUPERADMIN)
            throw new ApiException("NEED_PERMISSION_UPDATE_ROLE", HttpStatus.BAD_REQUEST);
        if (adminUser.getRole() != User.Role.ADMIN && adminUser.getRole() != User.Role.SUPERADMIN)
            throw new ApiException("NEED_PERMISSION_UPDATE_ROLE", HttpStatus.BAD_REQUEST);
        if ((user.getRole() == User.Role.ADMIN || newRole == User.Role.ADMIN) && adminUser.getRole() != User.Role.SUPERADMIN)
            throw new ApiException("NEED_PERMISSION_UPDATE_ROLE", HttpStatus.BAD_REQUEST);
        user.setRole(newRole);
        userRepository.save(user);
    }


    @Transactional
    public void createTag(String name, String nameCN) {
        if (tagRepository.findByName(name).orElse(null) == null && tagRepository.findByNameCN(nameCN).orElse(null) == null) {
            Tag newTag = new Tag();
            newTag.setName(name);
            newTag.setNameCN(nameCN);
            tagRepository.save(newTag);
        } else {
            throw new ApiException("TAG_EXISTS", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public void updateTag(long id, String name, String nameCN, Integer count) {
        if (tagRepository.findByName(name).orElse(null) != null || tagRepository.findByNameCN(nameCN).orElse(null) != null)
            throw new ApiException("TAG_EXISTS", HttpStatus.BAD_REQUEST);
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag == null)
            throw new ApiException("UNABLE_FOUND_TAG", HttpStatus.BAD_REQUEST);
        if (name == null) name = tag.getName();
        if (nameCN == null) nameCN = tag.getNameCN();
        if (count == null) count = tag.getTagUsage();
        tag.setName(name);
        tag.setNameCN(nameCN);
        tag.setTagUsage(count);
        tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(long id) {
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag == null)
            throw new ApiException("UNABLE_FOUND_TAG", HttpStatus.BAD_REQUEST);
        tagRepository.delete(tag);
    }

    @Transactional
    public void deleteAccount(User adminUser, long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) throw new ApiException("UNABLE_FOUND_USER", HttpStatus.BAD_REQUEST);
        if (user.getRole() == User.Role.SUPERADMIN)
            throw new ApiException("NEED_PERMISSION_DELETE_ACCOUNT", HttpStatus.BAD_REQUEST);
        if (adminUser.getRole() != User.Role.ADMIN && adminUser.getRole() != User.Role.SUPERADMIN)
            throw new ApiException("NEED_PERMISSION_DELETE_ACCOUNT", HttpStatus.BAD_REQUEST);
        if (user.getRole() == User.Role.ADMIN && adminUser.getRole() != User.Role.SUPERADMIN)
            throw new ApiException("NEED_PERMISSION_DELETE_ACCOUNT", HttpStatus.BAD_REQUEST);

        List<Novel> novels = novelRepository.queryNovelsByAuthor(user.getId());
        for (Novel novel : novels) {
            List<Long> tagIds = novel.getTags().stream().map(Tag::getId).toList();
            tagRepository.decreaseCount(tagIds);
            int chapterCount = novel.getChapterCount();
            int wordCount = novel.getWordCount();
            commentRepository.deleteRepliesByNovel(novel.getId());
            commentRepository.deleteCommentsByNovel(novel.getId());
            chapterRepository.deleteChaptersByNovel(novel.getId());
            likeRepository.deleteLikesByNovelId(novel.getId());
            favoriteRepository.deleteFavoritesByNovelId(novel.getId());
            repository.deleteNovelCover(novel, user.getId());
            novel.getTags().clear();
            novelRepository.save(novel);
            chapterRepository.deleteByNovelId(novel.getId());
            novelRepository.delete(novel);
            topTenService.removeFromTopTen(novel);
            recentNovelService.removeFromRecentNovels(novel);
            user.updateNovelCount(-1);
            user.updateChapterCount(-chapterCount);
            user.updateWordCount(-wordCount);
            userRepository.save(user);
        }
    }

    @Transactional
    public void deleteChapterById(User adminUser, long chapterId) {
        if (adminUser.getRole() != User.Role.ADMIN && adminUser.getRole() != User.Role.SUPERADMIN)
            throw new ApiException("NEED_PERMISSION_DELETE_CHAPTER", HttpStatus.BAD_REQUEST);
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if (chapter == null || chapter.getOrderIndex().compareTo(BigDecimal.ZERO) == 0)
            throw new ApiException("UNABLE_FOUND_CHAPTER", HttpStatus.BAD_REQUEST);
        Novel novel = chapter.getNovel();
        User user = novel.getAuthor();
        if (chapter.getContent() == null)
            fileService.uploadChapter(novel, user.getId(), chapter.getContentUrl(), "removal", true);
        int wordCount = chapter.getWordCount();
        commentRepository.deleteRepliesByChapter(chapterId);
        commentRepository.deleteCommentsByChapter(chapterId);
        chapterRepository.delete(chapter);

        if (chapter.isPublished()) {
            novel.setLastUpdatedAt(chapterRepository.now());
            novel.updateWordCount(-wordCount);
            novel.updateChapterCount(-1);
            user.updateWordCount(-wordCount);
            user.updateChapterCount(-1);
        }
        novelRepository.save(novel);
        topTenService.updateTopTen(novel);
        recentNovelService.updateRecentNovel(novel);
        userRepository.save(user);
    }

    @Transactional
    public void deleteCommentById(User adminUser, long commentId) {
        if (adminUser.getRole() != User.Role.ADMIN && adminUser.getRole() != User.Role.SUPERADMIN)
            throw new ApiException("NEED_PERMISSION_DELETE_COMMENT", HttpStatus.BAD_REQUEST);
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null)
            throw new ApiException("UNABLE_FOUND_COMMENT", HttpStatus.BAD_REQUEST);
        Comment parent = comment.getParent();
        if (parent != null)
            parent.updateCommentCount(-1);
        commentRepository.deleteCommentsByParentId(commentId);
        commentRepository.delete(comment);
    }

    @Transactional
    public Page<UserDTO> searchByUsername(String username, Pageable pageable) {
        Page<User> users = userRepository.searchByUsernamePrefix(username, pageable);
        return users.map(UserDTO::new);
    }

    @Transactional
    public Page<UserDTO> getAll(Pageable pageable) {
        Page<User> users = userRepository.getAll(pageable);
        return users.map(UserDTO::new);
    }

    @Transactional
    public Page<UserDTO> searchByUsernameRole(String username, User.Role role, Pageable pageable) {
        Page<User> users = userRepository.searchByUsernamePrefixRole(username, role, pageable);
        return users.map(UserDTO::new);
    }

    @Transactional
    public Page<UserDTO> getAllByRole(User.Role role, Pageable pageable) {
        Page<User> users = userRepository.getAllByRole(role, pageable);
        return users.map(UserDTO::new);
    }
}
