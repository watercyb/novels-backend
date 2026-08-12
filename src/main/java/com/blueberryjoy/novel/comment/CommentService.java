package com.blueberryjoy.novel.comment;

import com.blueberryjoy.novel.chpater.Chapter;
import com.blueberryjoy.novel.chpater.ChapterRepository;
import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ChapterRepository chapterRepository;

    @Transactional
    public CommentDTO createComment(User user, Number chapterIdNum, Number parentIdNum, String content) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setContent(content);
        if (chapterIdNum == null) {
            long parentId = parentIdNum.longValue();
            Comment parent = commentRepository.findById(parentId).orElse(null);
            if (parent == null)
                throw new ApiException("UNABLE_FOUND_COMMENT", HttpStatus.BAD_REQUEST);
            comment.setParent(parent);
            commentRepository.updateCommentCount(parentId, 1);
        } else {
            long chapterId = chapterIdNum.longValue();
            Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
            if (chapter == null)
                throw new ApiException("UNABLE_FOUND_CHAPTER", HttpStatus.BAD_REQUEST);
            comment.setChapter(chapter);
        }
        commentRepository.save(comment);
        return new CommentDTO(comment);
    }

    @Transactional
    public CommentDTO updateComment(User user, Number commentIdNum, String content) {
        long commentId = commentIdNum.longValue();
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || !Objects.equals(comment.getUser().getId(), user.getId()))
            throw new ApiException("UNABLE_FOUND_COMMENT", HttpStatus.BAD_REQUEST);
        comment.setContent(content);
        commentRepository.save(comment);
        return new CommentDTO(comment);
    }

    @Transactional
    public Page<CommentDTO> getCommentsByChapter(long chapterId, Pageable pageable) {
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if (chapter == null)
            throw new ApiException("UNABLE_FOUND_CHAPTER", HttpStatus.BAD_REQUEST);
        Page<Comment> page = commentRepository.findCommentsByChapter(chapterId, pageable);
        return page.map(CommentDTO::new);
    }

    @Transactional
    public List<CommentDTO> getCommentsByComment(long commentId) {
        List<Comment> list = commentRepository.findCommentsByParentId(commentId);
        return list.stream().map(CommentDTO::new).toList();
    }


    @Transactional
    public void deleteComment(User user, long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || !Objects.equals(comment.getUser().getId(), user.getId()))
            throw new ApiException("UNABLE_FOUND_COMMENT", HttpStatus.BAD_REQUEST);
        Comment parent = comment.getParent();
        if (parent != null)
            commentRepository.updateCommentCount(parent.getId(), -1);
        commentRepository.deleteCommentsByParentId(commentId);
        commentRepository.delete(comment);
    }
}