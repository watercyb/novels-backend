package com.blueberryjoy.novel;

import com.blueberryjoy.novel.chpater.ChapterService;
import com.blueberryjoy.novel.novel.NovelService;
import com.blueberryjoy.novel.user.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NovelApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(NovelApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        NovelService novelService = context.getBean(NovelService.class);
        ChapterService chapterService = context.getBean(ChapterService.class);
    }
}
