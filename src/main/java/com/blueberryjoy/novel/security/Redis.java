package com.blueberryjoy.novel.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.net.URI;

@Configuration
public class Redis {
    @Value("${UPSTASH_REDIS_REST_URI}")
    private String REDIS_URI;

    @Bean
    public Jedis getJedis() {
        return new Jedis(URI.create(REDIS_URI));
    }
}
