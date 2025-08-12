package com.example.hoteluiservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration; // 30 минут

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration; // 7 дней

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Извлечение всех claims
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Извлечение username
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Извлечение userId
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    // Извлечение email
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    // Извлечение role
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Проверка срока действия
    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true; // Считаем истекшим при ошибке
        }
    }

    // Валидация токена
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = extractUsername(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // Дополнительная валидация токена (без username)
    public boolean isValidToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(token) && claims.getSubject() != null;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Получить время истечения токена
    public Date getExpirationDate(String token) {
        try {
            return extractAllClaims(token).getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error getting token expiration: {}", e.getMessage());
            return new Date(); // Возвращаем текущее время при ошибке
        }
    }

    // Проверить, скоро ли истечет токен (за 5 минут до истечения)
    public boolean isTokenExpiringSoon(String token) {
        try {
            Date expiration = getExpirationDate(token);
            Date now = new Date();
            long timeLeft = expiration.getTime() - now.getTime();
            return timeLeft < 300000; // 5 минут в миллисекундах
        } catch (Exception e) {
            log.error("Error checking if token expiring soon: {}", e.getMessage());
            return true;
        }
    }
}