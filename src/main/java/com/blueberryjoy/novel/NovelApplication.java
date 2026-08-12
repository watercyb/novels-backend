package com.blueberryjoy.novel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NovelApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(NovelApplication.class, args);
    }
}
