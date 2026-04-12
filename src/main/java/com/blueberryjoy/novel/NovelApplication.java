package com.blueberryjoy.novel;

import com.blueberryjoy.novel.chpater.ChapterPreviewDTO;
import com.blueberryjoy.novel.chpater.ChapterService;
import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelPreviewDTO;
import com.blueberryjoy.novel.novel.NovelService;
import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class NovelApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(NovelApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        //User user = userService.createUser("cyb", "cyb@bbjdev.com", "hash123");
        User user = userService.getUser("cyb@bbjdev.com");
        System.out.println(user);


        NovelService novelService = context.getBean(NovelService.class);
        //novelService.createNovel("title", "desc", user, "");
        List<NovelPreviewDTO> novels = novelService.getNovels(user);
        System.out.println(novels.getFirst());

        Novel novel = novelService.getNovel(novels.getFirst().id());

        ChapterService chapterService = context.getBean(ChapterService.class);
        //chapterService.createChapter("3", novel, null, "content");
        List<ChapterPreviewDTO> chapters = chapterService.getChapters(novel);
        System.out.println(chapters);

        //chapterService.move(2L, 4L, 1L);
    }

}
