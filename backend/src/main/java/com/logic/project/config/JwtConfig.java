package com.logic.project.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtConfig {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtConfig(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expiration = expiration;

        System.out.println("===== JWT 설정 완료 =====");
        System.out.println("JWT 만료시간(ms): " + expiration);
    }

    // JWT 생성
    public String generateToken(String username, String role) {

        Date now = new Date();

        Date expiryDate =
                new Date(now.getTime() + expiration);

        String token = Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();

        System.out.println("===== JWT 생성 =====");
        System.out.println("username: " + username);
        System.out.println("role: " + role);
        System.out.println("발급시간: " + now);
        System.out.println("만료시간: " + expiryDate);

        // 토큰 자체는 보안상 출력하지 않음

        return token;
    }

    // JWT에서 username 가져오기
    public String getUsername(String token) {

        Claims claims = getClaims(token);

        return claims.getSubject();
    }

    // JWT에서 role 가져오기
    public String getRole(String token) {

        Claims claims = getClaims(token);

        return claims.get(
                "role",
                String.class
        );
    }

    // JWT 검증
    public boolean validateToken(String token) {

        try {

            Claims claims = getClaims(token);

            System.out.println("===== JWT 검증 성공 =====");
            System.out.println(
                    "username: " + claims.getSubject()
            );
            System.out.println(
                    "만료시간: " + claims.getExpiration()
            );

            return true;

        } catch (Exception e) {

            System.out.println("===== JWT 검증 실패 =====");
            System.out.println(
                    "오류 타입: " + e.getClass().getName()
            );
            System.out.println(
                    "오류 내용: " + e.getMessage()
            );

            return false;
        }
    }

    // JWT 내부 Claims 읽기
    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}