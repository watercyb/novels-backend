package com.blueberryjoy.novel.homepage;

import java.util.Map;


public record WeeklyViewData(int currentDayIndex, Map<Long, int[]> weeklyViews) {
}
