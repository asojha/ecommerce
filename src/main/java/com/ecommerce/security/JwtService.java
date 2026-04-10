package com.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirySeconds;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiry-seconds}") long expirySeconds) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirySeconds = expirySeconds;
    }

    public String generateToken(Long userId, String email) {
        long nowMs = System.currentTimeMillis();
        return Jwts.builder()
                .subject(email)
                .claim("uid", userId)
                .issuedAt(new Date(nowMs))
                .expiration(new Date(nowMs + expirySeconds * 1000))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return parseToken(token).getSubject();
    }

    public Long extractUserId(String token) {
        return parseToken(token).get("uid", Long.class);
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpirySeconds() {
        return expirySeconds;
    }
}
