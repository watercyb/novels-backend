package com.blueberryjoy.novel;

import com.blueberryjoy.novel.homepage.TopTenService;
import com.blueberryjoy.novel.novel.NovelDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NovelApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(NovelApplication.class, args);

        TopTenService topTenService = context.getBean(TopTenService.class);
        topTenService.increaseTodayCount(35);
        topTenService.increaseTodayCount(37);
        topTenService.increaseTodayCount(38);
        topTenService.increaseTodayCount(39);
        topTenService.increaseTodayCount(40);
        topTenService.updateTopTen();
        for (NovelDTO novelDTO : topTenService.getTopTen()) {
            System.out.println(novelDTO);
        }
    }
}
