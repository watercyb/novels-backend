package com.blueberryjoy.novel.chpater;

import com.blueberryjoy.novel.novel.Novel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ChapterService {

    private final ChapterRepository chapterRepository;

    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    public Chapter getChapter(Long id) {
        return chapterRepository.findById(id).orElse(null);
    }

    public Chapter createChapter(String title, Novel novel, String content_url, String content, BigDecimal orderIndex) {
        Chapter chapter = new Chapter();
        chapter.setTitle(title);
        chapter.setNovel(novel);
        chapter.setContentUrl(content_url);
        chapter.setContent(content);
        chapter.setOrderIndex(orderIndex);
        return chapterRepository.save(chapter);
    }

    public Chapter createChapter(String title, Novel novel, String content_url, String content) {
        Chapter chapter = new Chapter();
        chapter.setTitle(title);
        chapter.setNovel(novel);
        chapter.setContentUrl(content_url);
        chapter.setContent(content);
        chapter.setOrderIndex(getNextOrderIndex(novel.getId()));
        return chapterRepository.save(chapter);
    }

    public List<ChapterPreviewDTO> getChapters(Novel novel) {
        return chapterRepository.findChapterPreview(novel.getId());
    }

    public List<ChapterPreviewDTO> getChapters(long novelId) {
        return chapterRepository.findChapterPreview(novelId);
    }

    public BigDecimal getNextOrderIndex(long novelId) {
        BigDecimal lastIndex = chapterRepository.findLastOrderIndex(novelId);
        return lastIndex.setScale(0, RoundingMode.CEILING).add(BigDecimal.ONE);
    }

    //Error -> return null
    @Transactional
    public BigDecimal move(Long aId, Long bId, Long cId) {
        Object[] tupleA = chapterRepository.findNovelIdAndOrderIndexById(aId);
        Object[] tupleB = chapterRepository.findNovelIdAndOrderIndexById(bId);
        Object[] tupleC = chapterRepository.findNovelIdAndOrderIndexById(cId);
        if (tupleA == null || tupleB == null || tupleC == null) return null;
        tupleA = (Object[]) tupleA[0];
        tupleB = (Object[]) tupleB[0];
        tupleC = (Object[]) tupleC[0];
        if (tupleA == null || tupleB == null || tupleC == null) return null;

        Long novelIdA = (Long) tupleA[0];
        BigDecimal orderIndexA = (BigDecimal) tupleA[1];
        Long novelIdB = (Long) tupleB[0];
        BigDecimal orderIndexB = (BigDecimal) tupleB[1];
        Long novelIdC = (Long) tupleC[0];
        BigDecimal orderIndexC = (BigDecimal) tupleC[1];

        if (novelIdA == null || orderIndexA == null || orderIndexB == null || orderIndexC == null || !novelIdA.equals(novelIdB) || !novelIdB.equals(novelIdC) || orderIndexC.compareTo(BigDecimal.ZERO) == 0 || chapterRepository.existsBetween(novelIdA, orderIndexA, orderIndexB))
            return null;

        BigDecimal newIndex = orderIndexA
                .add(orderIndexB)
                .divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP);

        if (newIndex.compareTo(orderIndexA) == 0 || newIndex.compareTo(orderIndexB) == 0) {
            chapterRepository.shiftOrderIndexes(novelIdB, orderIndexB);
            newIndex = orderIndexB;
        }
        int res = chapterRepository.updateOrderIndex(cId, newIndex);
        if (res == 0) return null;
        return newIndex;
    }

    //Error -> return null
    @Transactional
    public BigDecimal moveToLast(long id) {
        Object[] tuple = chapterRepository.findNovelIdAndOrderIndexById(id);
        if (tuple == null) return null;
        tuple = (Object[]) tuple[0];
        if (tuple == null) return null;

        Long novelId = (Long) tuple[0];
        BigDecimal orderIndex = (BigDecimal) tuple[1];
        if (orderIndex.compareTo(BigDecimal.ZERO) == 0) return null;

        BigDecimal lastIndex = chapterRepository.findLastOrderIndex(novelId);
        if (orderIndex.compareTo(lastIndex) == 0) return lastIndex;
        BigDecimal newIndex = lastIndex.setScale(0, RoundingMode.CEILING).add(BigDecimal.ONE);

        int res = chapterRepository.updateOrderIndex(id, newIndex);
        if (res == 0) return null;
        return newIndex;
    }

    public boolean updateContext(long id, String content) {
        Chapter chapter = chapterRepository.findById(id).orElse(null);
        if (chapter == null) return false;
        chapter.setContent(content);
        chapterRepository.save(chapter);
        return true;
    }

    @Transactional
    public void deleteByNovelId(long novelId) {
        chapterRepository.deleteByNovelId(novelId);
    }
}
