package com.blueberryjoy.novel.homepage;

import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelDTO;
import com.blueberryjoy.novel.novel.NovelRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Service
public class TopTenService {
    @Autowired
    private NovelRepository novelRepository;

    private ConcurrentHashMap<Long, LongAdder> todayCounts = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private ConcurrentHashMap<Long, NovelDTO> topTens = new ConcurrentHashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void init() {
        WeeklyViewData weeklyViewData = readFile();
        Map<Long, int[]> weeklyViews = weeklyViewData.getWeeklyViews();
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(l -> l[1]));
        for (Map.Entry<Long, int[]> entry : weeklyViews.entrySet()) {
            long id = entry.getKey();
            int[] weeklyView = entry.getValue();
            int sum = Arrays.stream(weeklyView).sum();
            if (sum > 0) {
                pq.offer(new long[]{id, sum});
                if (pq.size() > 10) pq.poll();
            }
        }
        List<Long> ids = new ArrayList<>(pq.size());
        while (!pq.isEmpty()) {
            ids.add(pq.poll()[0]);
        }
        for (NovelDTO novelDTO : getNovels(ids)) {
            topTens.put(novelDTO.getId(), novelDTO);
        }
    }

    public void increaseTodayCount(long id) {
        todayCounts
                .computeIfAbsent(id, k -> new LongAdder())
                .increment();
    }

    @Transactional(readOnly = true)
    public void rotateWeeklyViews() {
        ConcurrentHashMap<Long, LongAdder> oldMap = todayCounts;
        todayCounts = new ConcurrentHashMap<>();
        WeeklyViewData weeklyViewData = readFile();
        int currentDayIndex = (weeklyViewData.getCurrentDayIndex() + 1) % 7;
        Map<Long, int[]> weeklyViews = weeklyViewData.getWeeklyViews();
        Map<Long, int[]> newWeeklyViews = new HashMap<>();
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(l -> l[1]));
        for (Map.Entry<Long, int[]> entry : weeklyViews.entrySet()) {
            long id = entry.getKey();
            int[] weeklyView = entry.getValue();
            weeklyView[currentDayIndex] = oldMap.getOrDefault(id, new LongAdder()).intValue();
            int sum = Arrays.stream(weeklyView).sum();
            if (sum > 0) {
                newWeeklyViews.put(id, weeklyView);
                pq.offer(new long[]{id, sum});
                if (pq.size() > 10) pq.poll();
            }
        }
        for (Map.Entry<Long, LongAdder> entry : oldMap.entrySet()) {
            long id = entry.getKey();
            int view = entry.getValue().intValue();
            if (!weeklyViews.containsKey(id)) {
                int[] weeklyView = new int[7];
                weeklyView[currentDayIndex] = view;
                newWeeklyViews.put(id, weeklyView);
                pq.offer(new long[]{id, view});
                if (pq.size() > 10) pq.poll();
            }
        }
        writeFile(new WeeklyViewData(currentDayIndex, newWeeklyViews));
        List<Long> ids = new ArrayList<>();
        while (!pq.isEmpty()) {
            ids.add(pq.poll()[0]);
        }
        ConcurrentHashMap<Long, NovelDTO> newTopTen = new ConcurrentHashMap<>();
        for (NovelDTO novelDTO : getNovels(ids)) {
            newTopTen.put(novelDTO.getId(), novelDTO);
        }
        topTens = newTopTen;
    }

    private WeeklyViewData readFile() {
        Path path = Path.of("data", "weeklyViews.json");
        if (Files.exists(path)) {
            return mapper.readValue(path.toFile(), WeeklyViewData.class);
        } else {
            WeeklyViewData weeklyViewData = new WeeklyViewData(0, new HashMap<>());
            writeFile(weeklyViewData);
            return weeklyViewData;
        }
    }

    private void writeFile(WeeklyViewData weeklyViewData) {
        try {
            Path path = Path.of("data", "weeklyViews.json");
            Files.createDirectories(path.getParent());
            mapper.writeValue(path.toFile(), weeklyViewData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<NovelDTO> getNovels(List<Long> ids) {
        List<Novel> novels = novelRepository.queryNovelsByIds(ids);
        return novels.stream().map(NovelDTO::new).toList();
    }

    public List<NovelDTO> getTopTen() {
        List<NovelDTO> topTenList = new ArrayList<>(topTens.size());
        for (NovelDTO novelDTO : topTens.values()) {
            topTenList.add(novelDTO.copy());
        }
        return topTenList;
    }

    public void updateTopTen(Novel novel) {
        if (topTens.containsKey(novel.getId())) topTens.put(novel.getId(), new NovelDTO(novel));
    }

    public void removeFromTopTen(Novel novel) {
        topTens.remove(novel.getId());
    }
}
