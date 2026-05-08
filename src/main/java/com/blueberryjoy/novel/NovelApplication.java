package com.blueberryjoy.novel;

import com.blueberryjoy.novel.mail.MailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NovelApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(NovelApplication.class, args);
    }
}
