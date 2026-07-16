package com.blueberryjoy.novel.homepage;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


@Getter
public class WeeklyViewData {
    private final int currentDayIndex;
    private Map<Long, int[]> weeklyViews = new HashMap<>();

    public WeeklyViewData(int currentDayIndex, Map<Long, int[]> weeklyViews) {
        this.currentDayIndex = currentDayIndex;
        this.weeklyViews = weeklyViews;
    }
}
