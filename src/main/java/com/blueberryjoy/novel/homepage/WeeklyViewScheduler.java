package com.blueberryjoy.novel.homepage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class WeeklyViewScheduler {
    @Autowired
    private TopTenService topTenService;

    @Scheduled(cron = "0 0 0 * * *")
    public void rotateWeeklyViews() {
        topTenService.rotateWeeklyViews();
    }
}
