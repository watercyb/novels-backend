package com.blueberryjoy.novel;

import com.blueberryjoy.novel.admin.AdminService;
import com.blueberryjoy.novel.novel.TypeCount;
import com.blueberryjoy.novel.user.RoleCount;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class NovelApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(NovelApplication.class, args);
    }
}
