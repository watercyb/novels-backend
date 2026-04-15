package com.blueberryjoy.novel.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${REFRESH_SECRET}")
    private String refreshSecret;
    private SecretKey REFRESH_KEY;

    @PostConstruct
    public void init() {
        REFRESH_KEY = Keys.hmacShaKeyFor(refreshSecret.getBytes());
    }

    private final SecretKey ACCESS_KEY = Jwts.SIG.HS256.key().build();

    public String generateAccessToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(ACCESS_KEY)
                .compact();
    }

    public String getEmailFromAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(ACCESS_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000))
                .signWith(REFRESH_KEY)
                .compact();
    }

    public String getEmailFromRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(REFRESH_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public ResponseCookie getAccessCookie(String email) {
        String accessToken = generateAccessToken(email);
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)      // ❌ JS cannot access it
                .secure(false)       // true in production (HTTPS)
                .path("/api/")           // available for all endpoints
                .maxAge(15 * 60)     // 15 min
                .sameSite("Strict")  // CSRF protection
                .build();
    }

    public ResponseCookie getRefreshCookie(String email) {
        String refreshToken = generateRefreshToken(email);
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)      // ❌ JS cannot access it
                .secure(false)       // true in production (HTTPS)
                .path("/api/auth/refresh")           // available for all endpoints
                .maxAge(14 * 24 * 60 * 60)     // 14 day
                .sameSite("Strict")  // CSRF protection
                .build();
    }
}