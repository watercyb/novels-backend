package com.blueberryjoy.novel;

import com.blueberryjoy.novel.chpater.ChapterService;
import com.blueberryjoy.novel.novel.Novel;
import com.blueberryjoy.novel.novel.NovelDTO;
import com.blueberryjoy.novel.novel.NovelRepository;
import com.blueberryjoy.novel.novel.NovelService;
import com.blueberryjoy.novel.tag.Tag;
import com.blueberryjoy.novel.tag.TagService;
import com.blueberryjoy.novel.user.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class NovelApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(NovelApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        //User user = userService.createUser("cyb", "cyb@bbjdev.com", "hash123");
        //User user = userService.getUser("cyb@bbjdev.com");
        //System.out.println(user);


        NovelService novelService = context.getBean(NovelService.class);
        //novelService.createNovel("title", "desc", user, "");
        //List<NovelDTO> novels = novelService.getNovels(user);
        //System.out.println(novels);

        //NovelDTO novelDTO = novelService.getNovelDTO(novels.getFirst().getId());
        //System.out.println(novelDTO);

        ChapterService chapterService = context.getBean(ChapterService.class);
        //chapterService.createChapter("3", novel, null, "content");
        //List<ChapterPreviewDTO> chapters = chapterService.getChapters(novelDTO.getId());
        //System.out.println("好".getBytes(StandardCharsets.UTF_8).length);

        //TagService tagService = context.getBean(TagService.class);
        //Tag res = tagService.createTag("action");
        //System.out.println(res);

        //novelService.removeTag(1L, 1L);


        //chapterService.updateContext(2L, "a");

        //novelService.deleteNovel(novels.getFirst().getId());

        //novelService.addTag(3L, 1L);
        //novelService.createNovel("aaa ccc bbb", "d", 4L, "");
        //novelService.createNovel("bbb ccc aaa", "d", 4L, "");
        //novelService.deleteNovel(2L);
        //Page<NovelDTO> res = novelService.searchByKeywordAndTag("ccc aaa", List.of("action", "dungeon"), 0, 10);
        Page<NovelDTO> res = novelService.searchByTags(List.of("action", "dungeon"), 0, 10);
        System.out.println(res.getTotalPages() + " " + res.getTotalElements());
        for (NovelDTO novel : res.getContent()) {
            System.out.println(novel);
        }
    }
}
