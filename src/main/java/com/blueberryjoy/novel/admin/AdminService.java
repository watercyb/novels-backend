package com.blueberryjoy.novel.admin;

import com.blueberryjoy.novel.chpater.ChapterRepository;
import com.blueberryjoy.novel.comment.CommentRepository;
import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.favorite.FavoriteRepository;
import com.blueberryjoy.novel.file.FileService;
import com.blueberryjoy.novel.homepage.TopTenService;
import com.blueberryjoy.novel.like.LikeRepository;
import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelRepository;
import com.blueberryjoy.novel.tag.Tag;
import com.blueberryjoy.novel.tag.TagRepository;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void deleteById(User adminUser, long novelId) {
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
    public void update(long id, String name, String nameCN, Integer count) {
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
}
