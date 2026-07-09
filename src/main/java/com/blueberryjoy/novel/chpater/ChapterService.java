package com.blueberryjoy.novel.chpater;

import com.blueberryjoy.novel.comment.CommentRepository;
import com.blueberryjoy.novel.exception.ApiException;
import com.blueberryjoy.novel.file.FileService;
import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelRepository;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserRepository;
import org.jsoup.Jsoup;
import org.owasp.html.PolicyFactory;
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
    @Autowired
    private PolicyFactory policyFactory;

    public List<ChapterDTO> getChapter(User user, Long id, boolean adjacent, boolean published) {
        Chapter chapter = chapterRepository.findById(id).orElse(null);
        if (chapter == null || chapter.getOrderIndex().compareTo(BigDecimal.ZERO) == 0 || (!chapter.isPublished() && published) || (!chapter.isPublished() && !Objects.equals(chapter.getNovel().getAuthor().getId(), user.getId())))
            throw new ApiException("UNABLE_FOUND_CHAPTER", HttpStatus.BAD_REQUEST);
        List<ChapterDTO> res = new ArrayList<>();
        ChapterDTO chapterDTO = new ChapterDTO(chapter, true);
        if (chapter.getContent() == null && chapter.getContentUrl() != null) {
            chapterDTO.setContent(fileService.getChapter(chapter.getContentUrl()));
        }
        res.add(chapterDTO);
        if (published) {
            if (adjacent) {
                ChapterDTO prv = chapterRepository.findPrevPublishedChapterDTO(chapter.getNovel().getId(), chapter.getOrderIndex(), PageRequest.of(0, 1)).stream().findFirst().orElse(null);
                ChapterDTO next = chapterRepository.findNextPublishedChapterDTO(chapter.getNovel().getId(), chapter.getOrderIndex(), PageRequest.of(0, 1)).stream().findFirst().orElse(null);
                res.add(prv);
                res.add(next);
            }
        } else {
            if (adjacent) {
                ChapterDTO prv = chapterRepository.findPrevChapterDTO(chapter.getNovel().getId(), chapter.getOrderIndex(), PageRequest.of(0, 1)).stream().findFirst().orElse(null);
                ChapterDTO next = chapterRepository.findNextChapterDTO(chapter.getNovel().getId(), chapter.getOrderIndex(), PageRequest.of(0, 1)).stream().findFirst().orElse(null);
                res.add(prv);
                res.add(next);
            }
        }
        return res;
    }

    @Transactional
    public ChapterDTO createChapter(User user, long novelId, String title, String content) {
        if (title == null || title.isEmpty() || content == null)
            throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null || !Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
        content = sanitizeHtml(content);
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
        chapter.setPublished(false);
        chapterRepository.save(chapter);
        return new ChapterDTO(chapter.getId(), chapter.getNovel().getId(), chapter.getTitle(), chapter.getOrderIndex(), chapter.isPublished());
    }

    @Transactional
    public ChapterDTO updateChapter(User user, long novelId, long chapterId, String title, String content) {
        if ((title == null || title.isEmpty()) && (content == null || content.isEmpty()))
            throw new ApiException("ALL_FIELDS_REQUIRED", HttpStatus.BAD_REQUEST);
        Novel novel = novelRepository.findById(novelId).orElse(null);
        if (novel == null || !Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if (chapter == null || !Objects.equals(chapter.getNovel().getId(), novel.getId()) || chapter.getOrderIndex().compareTo(BigDecimal.ZERO) == 0)
            throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
        if (title != null && !title.isEmpty())
            chapter.setTitle(title);
        if (content != null && !content.isEmpty()) {
            content = sanitizeHtml(content);
            int wordCountOld = chapter.getWordCount();
            int wordCountNew = countWordsEnZh(content);
            int size = content.getBytes(StandardCharsets.UTF_8).length;
            if (size > 30000) {
                String url = fileService.uploadChapter(novel, user.getId(), chapter.getContentUrl(), content, false);
                chapter.setContent(null);
                chapter.setContentUrl(url);
            } else {
                chapter.setContent(content);
                if (chapter.getContentUrl() != null) {
                    fileService.uploadChapter(novel, user.getId(), chapter.getContentUrl(), content, true);
                    chapter.setContentUrl(null);
                }
            }
            chapter.setWordCount(wordCountNew);
            chapterRepository.save(chapter);
            if (chapter.isPublished()) {
                novel.updateWordCount(wordCountNew - wordCountOld);
                user.updateWordCount(wordCountNew - wordCountOld);
                novel.setLastUpdatedAt(chapter.getUpdatedAt());
                novel.setLastChapterTitle(chapter.getTitle());
            }
        }
        novel.setLastChapterTitle(chapter.getTitle());
        novelRepository.save(novel);
        userRepository.save(user);
        return new ChapterDTO(chapter.getId(), chapter.getNovel().getId(), chapter.getTitle(), chapter.getOrderIndex(), chapter.isPublished());
    }

    public Page<ChapterDTO> getChapters(User user, long novelId, Pageable pageable, boolean published) {
        if (published) {
            return chapterRepository.findPublishedChapterPreview(novelId, pageable);
        } else {
            Novel novel = novelRepository.findById(novelId).orElse(null);
            if (novel == null || !Objects.equals(novel.getAuthor().getId(), user.getId()))
                throw new ApiException("UNABLE_FOUND_NOVEL", HttpStatus.BAD_REQUEST);
            return chapterRepository.findChapterPreview(novelId, pageable);
        }
    }

    public BigDecimal getNextOrderIndex(long novelId) {
        BigDecimal lastIndex = chapterRepository.findLastOrderIndex(novelId);
        return lastIndex.setScale(0, RoundingMode.CEILING).add(BigDecimal.ONE);
    }

    //Move target after chapter.
    @Transactional
    public ChapterDTO move(User user, long chapterId, long targetId) {
        if (chapterId == targetId) throw new ApiException("ACTION_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        Chapter target = chapterRepository.findById(targetId).orElse(null);
        if (chapter == null || target == null || target.getOrderIndex().compareTo(BigDecimal.ZERO) == 0)
            throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
        ChapterDTO next = chapterRepository.findNextChapterDTO(chapter.getNovel().getId(), chapter.getOrderIndex(), PageRequest.of(0, 1)).stream().findFirst().orElse(null);
        Novel novelChapter = chapter.getNovel();
        Novel novelTarget = target.getNovel();
        if (!novelChapter.getAuthor().getId().equals(user.getId()) || !novelTarget.getAuthor().getId().equals(user.getId()))
            throw new ApiException("You do not have permission to move this chapter.", HttpStatus.BAD_REQUEST);
        if (next == null) {
            if (chapter.getOrderIndex().compareTo(target.getOrderIndex()) == 0)
                return new ChapterDTO(target.getId(), target.getNovel().getId(), target.getTitle(), target.getOrderIndex(), chapter.isPublished());
            BigDecimal newIndex = chapter.getOrderIndex().setScale(0, RoundingMode.CEILING).add(BigDecimal.ONE);
            target.setOrderIndex(newIndex);
            chapterRepository.save(target);
        } else {
            if (!Objects.requireNonNull(novelRepository.findById(next.getNovelId()).orElse(null)).getAuthor().getId().equals(user.getId()))
                throw new ApiException("You do not have permission to move this chapter.", HttpStatus.BAD_REQUEST);
            if (next.getId() == targetId)
                return new ChapterDTO(target.getId(), target.getNovel().getId(), target.getTitle(), target.getOrderIndex(), chapter.isPublished());
            BigDecimal orderIndexChapter = chapter.getOrderIndex();
            BigDecimal orderIndexNext = next.getOrderIndex();
            BigDecimal newIndex = orderIndexChapter.add(orderIndexNext).divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP);
            if (newIndex.compareTo(orderIndexChapter) == 0 || newIndex.compareTo(orderIndexNext) == 0) {
                chapterRepository.shiftOrderIndexes(next.getId(), orderIndexNext);
                newIndex = orderIndexNext;
            }
            target.setOrderIndex(newIndex);
            chapterRepository.save(target);
        }
        return new ChapterDTO(target.getId(), target.getNovel().getId(), target.getTitle(), target.getOrderIndex(), chapter.isPublished());
    }

    @Transactional
    public void deleteChapter(User user, long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if (chapter == null || chapter.getOrderIndex().compareTo(BigDecimal.ZERO) == 0)
            throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
        Novel novel = chapter.getNovel();
        if (!Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("You do not have permission to delete this chapter.", HttpStatus.BAD_REQUEST);
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

        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {

            String word = text.substring(start, end);

            if (word.codePoints().anyMatch(Character::isLetterOrDigit)) count++;
        }

        return count;
    }

    public static int countWordsEnZh(String text) {
        if (text == null || text.isBlank()) return 0;
        text = Jsoup.parse(text).text();

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
                    if (Character.UnicodeScript.of(next) == Character.UnicodeScript.HAN) break;
                    j += Character.charCount(next);
                }

                String segment = text.substring(i, j);
                count += countWords(segment, Locale.ENGLISH);

                i = j;
            }
        }
        return count;
    }

    @Transactional
    public void publishChapter(User user, long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if (chapter == null) throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
        Novel novel = chapter.getNovel();
        if (novel == null || !Objects.equals(novel.getAuthor().getId(), user.getId()))
            throw new ApiException("Chapter not found", HttpStatus.BAD_REQUEST);
        chapter.setPublished(true);
        chapterRepository.save(chapter);
        int wordCount = chapter.getWordCount();
        novel.updateChapterCount(1);
        novel.updateWordCount(wordCount);
        novel.setLastUpdatedAt(chapter.getUpdatedAt());
        novel.setLastChapterTitle(chapter.getTitle());
        novelRepository.save(novel);
        user.updateChapterCount(1);
        user.updateWordCount(wordCount);
        userRepository.save(user);
    }

    public String sanitizeHtml(String rawHtml) {
        return policyFactory.sanitize(rawHtml);
    }

}
