package com.blueberryjoy.novel.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateFilter extends OncePerRequestFilter {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if ("POST".equalsIgnoreCase(request.getMethod()) && "/api/auth/signup".equals(path)) {
            String ip = request.getRemoteAddr();
            String key = "Novels_SignUp_" + ip;
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj == null) {
                redisTemplate.opsForValue().set(key, String.valueOf(1), Duration.ofMinutes(600));
            } else {
                int val = Integer.parseInt(obj.toString());
                if (val < 10) {
                    Long ttl = redisTemplate.getExpire(key);
                    if (ttl != null && ttl > 0) {
                        redisTemplate.opsForValue().set(key, String.valueOf(val + 1), Duration.ofSeconds(ttl));
                    } else {
                        redisTemplate.opsForValue().set(key, String.valueOf(val + 1), Duration.ofMinutes(600));
                    }
                } else {
                    response.setStatus(429);
                    response.getWriter().write("{\"success\":false,\"message\":\"TOO_MANY_REQUESTES\"}");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
