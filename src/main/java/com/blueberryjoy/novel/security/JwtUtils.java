package com.blueberryjoy.novel.security;

import com.blueberryjoy.novel.user.User;
import com.blueberryjoy.novel.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtils {

    @Value("${REFRESH_SECRET}")
    private String refreshSecret;
    private SecretKey REFRESH_KEY;
    @Value("${MODE}")
    private String MODE;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        REFRESH_KEY = Keys.hmacShaKeyFor(refreshSecret.getBytes());
    }

    private final SecretKey ACCESS_KEY = Jwts.SIG.HS256.key().build();

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("verification", getLastFourDigits(user.getPasswordHash()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                .signWith(ACCESS_KEY)
                .compact();
    }

    public User verifyAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(ACCESS_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Integer userId = claims.get("userId", Integer.class);
            String verification = claims.get("verification", String.class);
            if (userId == null || verification == null)
                return null;
            User user = userService.findUserById(userId);
            if (user == null || !getLastFourDigits(user.getPasswordHash()).equals(verification))
                return null;
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000))
                .signWith(REFRESH_KEY)
                .compact();
    }

    public User verifyRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(REFRESH_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Integer userId = claims.get("userId", Integer.class);
            if (userId == null)
                return null;
            String savedToken = Objects.requireNonNull(redisTemplate.opsForValue().get("Novels_RefreshToken_" + userId)).toString();
            if (!Objects.equals(savedToken, token)) return null;
            return userService.findUserById(userId);
        } catch (Exception e) {
            return null;
        }
    }

    public ResponseCookie getAccessCookie(User user) {
        String accessToken = generateAccessToken(user);
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)      // ❌ JS cannot access it
                .secure(!MODE.equals("DEVELOPMENT"))       // true in production (HTTPS)
                .path("/api/")           // available for all endpoints
                .maxAge(7 * 24 * 60 * 60)     // 7 day
                .sameSite("Strict")  // CSRF protection
                .build();
    }

    public ResponseCookie getRefreshCookie(User user) {
        String refreshToken = generateRefreshToken(user);
        redisTemplate.opsForValue().set("Novels_RefreshToken_" + user.getId(), refreshToken, Duration.ofMinutes(14 * 24 * 60 * 60));

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(!MODE.equals("DEVELOPMENT"))
                .path("/api/auth/refresh")
                .maxAge(14 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie getEmptyAccessCookie() {
        return ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(!MODE.equals("DEVELOPMENT"))
                .path("/api/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie getEmptyRefreshCookie(@Nullable User user) {
        if (user != null)
            redisTemplate.delete("Novels_RefreshToken_" + user.getId());
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(!MODE.equals("DEVELOPMENT"))
                .path("/api/auth/refresh")
                .maxAge(14 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
    }


    public String getLastFourDigits(String str) {
        return str.substring(str.length() - 4);
    }
}