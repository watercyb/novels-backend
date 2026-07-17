package com.blueberryjoy.novel.homepage;

import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelDTO;
import com.blueberryjoy.novel.novel.NovelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.PriorityBlockingQueue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RecentNovelService {
    @Autowired
    private NovelRepository novelRepository;

    private final ConcurrentHashMap<Long, NovelDTO> recentNovels = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<long[]> pq = new PriorityBlockingQueue<>(10, Comparator.comparingLong(a -> a[1]));

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void init() {
        recentNovels.clear();
        pq.clear();
        List<Novel> novels = novelRepository.queryRecent10Novels();
        for (Novel novel : novels) {
            NovelDTO novelDTO = new NovelDTO(novel);
            recentNovels.put(novel.getId(), novelDTO);
            pq.add(new long[]{novel.getId(), novelDTO.getLastUpdatedAtTime()});
        }
    }

    public void updateRecentNovel(Novel novel) {
        long id = novel.getId();
        NovelDTO newNovelDTO = new NovelDTO(novel);
        if (recentNovels.containsKey(id)) {
            NovelDTO novelDTO = recentNovels.get(id);
            recentNovels.put(id, newNovelDTO);
            if (novelDTO.getLastUpdatedAt() != newNovelDTO.getLastUpdatedAt())
                pq.offer(new long[]{id, newNovelDTO.getLastUpdatedAtTime()});
        } else {
            recentNovels.put(id, newNovelDTO);
            pq.offer(new long[]{id, newNovelDTO.getLastUpdatedAtTime()});
            if (recentNovels.size() > 10) {
                clear();
                if (!pq.isEmpty()) {
                    long[] pair = pq.poll();
                    recentNovels.remove(pair[0]);
                }
            }
        }
    }

    private void clear() {
        while (!pq.isEmpty()) {
            long[] pair = pq.peek();
            if (!recentNovels.containsKey(pair[0]) || pair[1] != recentNovels.get(pair[0]).getLastUpdatedAtTime()) {
                pq.poll();
            } else {
                break;
            }
        }
    }

    public List<NovelDTO> getRecentNovels() {
        List<NovelDTO> recentNovelList = new ArrayList<>(recentNovels.size());
        for (NovelDTO novelDTO : recentNovels.values()) {
            recentNovelList.add(novelDTO.copy());
        }
        return recentNovelList;
    }

    public void removeFromRecentNovels(Novel novel) {
        recentNovels.remove(novel.getId());
    }
}
