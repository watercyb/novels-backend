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
import java.util.Objects;

@Component
public class RateFilter extends OncePerRequestFilter {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if ("POST".equalsIgnoreCase(request.getMethod()) && "/api/auth/signup".equals(path)) {
            String ip = request.getRemoteAddr();
            Object obj = redisTemplate.opsForValue().get("Novels_SignUp_" + ip);
            if (obj == null) {
                redisTemplate.opsForValue().set("Novels_SignUp_" + ip, String.valueOf(1), Duration.ofMinutes(24 * 60 * 60));
            } else {
                int val = Integer.parseInt(obj.toString());
                if (val < 5) {
                    redisTemplate.opsForValue().set("Novels_SignUp_" + ip, String.valueOf(val + 1));
                } else {
                    response.setStatus(429);
                    response.getWriter().write("{\"success\":false,\"message\":\"Too many requests\"}");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
