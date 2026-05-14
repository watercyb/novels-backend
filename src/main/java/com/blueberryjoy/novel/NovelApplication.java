package com.blueberryjoy.novel;

import com.blueberryjoy.novel.mail.MailService;
import com.blueberryjoy.novel.security.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@SpringBootApplication
public class NovelApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(NovelApplication.class, args);

        RedisTemplate<String, Object> redisTemplate = context.getBean("redisTemplate", RedisTemplate.class);
        redisTemplate.opsForValue().set("key", "value");
    }
}
