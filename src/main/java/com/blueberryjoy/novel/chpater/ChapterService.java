package com.blueberryjoy.novel.chpater;

import com.blueberryjoy.novel.comment.CommentRepository;
import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.file.FileService;
import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelRepository;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.*;

@Service
public class ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    public List<ChapterDTO> getChapter(Long id, boolean adjacent) {
        Chapter chapter = chapterRepository.findById(id).orElse(null);
        if (chapter == null || chapter.getOrderIndex().compareTo(BigDecimal.ZERO) == 0)
            throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
        List<ChapterDTO> res = new ArrayList<>();
        res.add(new ChapterDTO(chapter));
        if (adjacent) {
            ChapterDTO prv = chapterRepository
                    .findPrevChapterDTO(chapter.getNovel().getId(), chapter.getOrderIndex(), PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .orElse(null);
            ChapterDTO next = chapterRepository
                    .findNextChapterDTO(chapter.getNovel().getId(), chapter.getOrderIndex(), PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .orElse(null);
            res.add(prv);
            res.add(next);
        }
        return res;
    }

    @Transactional
    public ChapterDTO createChapter(User user, long novelId, String title, String content) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null || !Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
        int wordCount = countWordsEnZh(content);
        int size = content.getBytes(StandardCharsets.UTF_8).length;
        Chapter chapter = new Chapter();
        chapter.setTitle(title);
        chapter.setNovel(novel);
        if (size > 30000) {
            String url = fileService.uploadChapter(novel, user.getId(), null, content, false);
            chapter.setContentUrl(url);
        } else {
            chapter.setContent(content);
        }
        chapter.setOrderIndex(getNextOrderIndex(novel.getId()));
        chapter.setWordCount(wordCount);
        chapterRepository.save(chapter);
        novel.updateChapterCount(1);
        novel.updateWordCount(wordCount);
        novelRepository.save(novel);
        user.updateChapterCount(1);
        user.updateWordCount(wordCount);
        userRepository.save(user);
        return new ChapterDTO(chapter.getId(), chapter.getNovel().getId(), chapter.getTitle(), chapter.getOrderIndex());
    }


    @Transactional
    public ChapterDTO updateChapter(User user, long novelId, long chapterId, String title, String content) {
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null || !Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("Novel not found", HttpStatus.BAD_REQUEST);
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if (chapter == null || !Objects.equals(chapter.getNovel().getId(), novel.getId()) || chapter.getOrderIndex().compareTo(BigDecimal.ZERO) == 0)
            throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
        int wordCountOld = chapter.getWordCount();
        int wordCountNew = countWordsEnZh(content);
        int size = content.getBytes(StandardCharsets.UTF_8).length;
        chapter.setTitle(title);
        if (size > 30000) {
            String url = fileService.uploadChapter(novel, user.getId(), null, content, false);
            chapter.setContent(null);
            chapter.setContentUrl(url);
        } else {
            chapter.setContent(content);
            chapter.setContentUrl(null);
        }
        chapter.setWordCount(wordCountNew);
        chapterRepository.save(chapter);
        novel.updateWordCount(wordCountNew - wordCountOld);
        novel.setLastChapterTitle(title);
        novel.setLastUpdatedAt(chapter.getUpdatedAt());
        novelRepository.save(novel);
        user.updateWordCount(wordCountNew - wordCountOld);
        userRepository.save(user);
        return new ChapterDTO(chapter.getId(), chapter.getNovel().getId(), chapter.getTitle(), chapter.getOrderIndex());
    }

    public Page<ChapterDTO> getChapters(long novelId, Pageable pageable) {
        return chapterRepository.findChapterPreview(novelId, pageable);
    }

    public BigDecimal getNextOrderIndex(long novelId) {
        BigDecimal lastIndex = chapterRepository.findLastOrderIndex(novelId);
        return lastIndex.setScale(0, RoundingMode.CEILING).add(BigDecimal.ONE);
    }

    //Move target after chapter.
    @Transactional
    public ChapterDTO move(User user, long chapterId, long targetId) {
        if (chapterId == targetId)
            throw new ApiException("This action isn’t allowed.", HttpStatus.BAD_REQUEST);
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        Chapter target = chapterRepository.findById(targetId).orElse(null);
        if (chapter == null || target == null || target.getOrderIndex().compareTo(BigDecimal.ZERO) == 0)
            throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
        ChapterDTO next = chapterRepository
                .findNextChapterDTO(chapter.getNovel().getId(), chapter.getOrderIndex(), PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
        Novel novelChapter = chapter.getNovel();
        Novel novelTarget = target.getNovel();
        if (!novelChapter.getAuthor().getId().equals(user.getId()) || !novelTarget.getAuthor().getId().equals(user.getId()))
            throw new ApiException("You do not have permission to move this chapter.", HttpStatus.BAD_REQUEST);
        if (next == null) {
            if (chapter.getOrderIndex().compareTo(target.getOrderIndex()) == 0)
                return new ChapterDTO(target.getId(), target.getNovel().getId(), target.getTitle(), target.getOrderIndex());
            BigDecimal newIndex = chapter.getOrderIndex().setScale(0, RoundingMode.CEILING).add(BigDecimal.ONE);
            target.setOrderIndex(newIndex);
            chapterRepository.save(target);
        } else {
            if (!Objects.requireNonNull(novelRepository.findById(next.getNovelId()).orElse(null)).getAuthor().getId().equals(user.getId()))
                throw new ApiException("You do not have permission to move this chapter.", HttpStatus.BAD_REQUEST);
            if (next.getId() == targetId)
                return new ChapterDTO(target.getId(), target.getNovel().getId(), target.getTitle(), target.getOrderIndex());
            BigDecimal orderIndexChapter = chapter.getOrderIndex();
            BigDecimal orderIndexNext = next.getOrderIndex();
            BigDecimal newIndex = orderIndexChapter
                    .add(orderIndexNext)
                    .divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP);
            if (newIndex.compareTo(orderIndexChapter) == 0 || newIndex.compareTo(orderIndexNext) == 0) {
                chapterRepository.shiftOrderIndexes(next.getId(), orderIndexNext);
                newIndex = orderIndexNext;
            }
            target.setOrderIndex(newIndex);
            chapterRepository.save(target);
        }
        return new ChapterDTO(target.getId(), target.getNovel().getId(), target.getTitle(), target.getOrderIndex());
    }

    @Transactional
    public void deleteChapter(User user, long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if (chapter == null || chapter.getOrderIndex().compareTo(BigDecimal.ZERO) == 0)
            throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
        Novel novel = chapter.getNovel();
        if (!Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("You do not have permission to delete this chapter.", HttpStatus.BAD_REQUEST);
        if (chapter.getContent() == null || chapter.getContent().isEmpty())
            fileService.uploadChapter(novel, user.getId(), chapter.getContentUrl(), "", true);
        int wordCount = chapter.getWordCount();
        commentRepository.deleteRepliesByChapter(chapterId);
        commentRepository.deleteCommentsByChapter(chapterId);
        chapterRepository.delete(chapter);
        novel.updateWordCount(-wordCount);
        novel.setLastUpdatedAt(chapter.getUpdatedAt());
        novel.updateChapterCount(-1);
        novelRepository.save(novel);
        user.updateWordCount(-wordCount);
        user.updateChapterCount(-1);
        userRepository.save(user);
    }

    private static int countWords(String text, Locale locale) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        BreakIterator iterator = BreakIterator.getWordInstance(locale);
        iterator.setText(text);

        int count = 0;
        int start = iterator.first();

        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {

            String word = text.substring(start, end);

            if (word.codePoints().anyMatch(Character::isLetterOrDigit))
                count++;
        }

        return count;
    }

    public static int countWordsEnZh(String text) {
        if (text == null || text.isBlank()) return 0;

        int count = 0;

        for (int i = 0; i < text.length(); ) {
            int cp = text.codePointAt(i);

            if (Character.UnicodeScript.of(cp) == Character.UnicodeScript.HAN) {
                count++; // count each Chinese character
                i += Character.charCount(cp);
            } else {
                // handle English chunk
                int j = i;
                while (j < text.length()) {
                    int next = text.codePointAt(j);
                    if (Character.UnicodeScript.of(next) == Character.UnicodeScript.HAN)
                        break;
                    j += Character.charCount(next);
                }

                String segment = text.substring(i, j);
                count += countWords(segment, Locale.ENGLISH);

                i = j;
            }
        }

        return count;
    }
}
