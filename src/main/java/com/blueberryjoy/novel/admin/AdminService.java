package com.blueberryjoy.novel.admin;

import com.blueberryjoy.novel.chpater.ChapterRepository;
import com.blueberryjoy.novel.comment.CommentRepository;
import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.favorite.FavoriteRepository;
import com.blueberryjoy.novel.file.FileService;
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

    @Transactional
    public void deleteById(User adminUser, long novelId) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null) throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
        if (adminUser.getRole() != User.Role.ADMIN && adminUser.getRole() != User.Role.SUPERADMIN)
            throw new ApiException("You do not have permission to delete this novel.", HttpStatus.BAD_REQUEST);
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
        user.updateNovelCount(-1);
        user.updateChapterCount(-chapterCount);
        user.updateWordCount(-wordCount);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserRole(User adminUser, long userId, User.Role newRole) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) throw new ApiException("User not found", HttpStatus.BAD_REQUEST);
        if (user.getRole() == User.Role.SUPERADMIN || newRole == User.Role.SUPERADMIN)
            throw new ApiException("You do not have permission to update the role of this user.", HttpStatus.BAD_REQUEST);
        if (adminUser.getRole() != User.Role.ADMIN && adminUser.getRole() != User.Role.SUPERADMIN)
            throw new ApiException("You do not have permission to update the role of this user.", HttpStatus.BAD_REQUEST);
        if ((user.getRole() == User.Role.ADMIN || newRole == User.Role.ADMIN) && adminUser.getRole() != User.Role.SUPERADMIN)
            throw new ApiException("You do not have permission to update the role of this user.", HttpStatus.BAD_REQUEST);
        user.setRole(newRole);
        userRepository.save(user);
    }
}
