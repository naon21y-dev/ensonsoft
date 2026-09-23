package com.logic.project.security;

import com.logic.project.config.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 현재 요청 주소
        String requestUri = request.getRequestURI();

        // Authorization 헤더 가져오기
        String authorizationHeader = request.getHeader("Authorization");

        // Bearer 토큰이 없는 경우
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            System.out.println("===== JWT 헤더 없음 =====");
            System.out.println("요청 URI: " + requestUri);

            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 부분 제거
        String token = authorizationHeader.substring(7);

        try {

            // JWT 유효성 검사
            boolean valid = jwtConfig.validateToken(token);

            System.out.println("===== JWT 검사 =====");
            System.out.println("요청 URI: " + requestUri);
            System.out.println("JWT 유효 여부: " + valid);

            if (valid) {

                // JWT에서 username 추출
                String username = jwtConfig.getUsername(token);

                System.out.println("JWT username: " + username);

                // 아직 인증되지 않은 경우
                if (username != null &&
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication() == null) {

                    // DB에서 사용자 조회
                    UserDetails userDetails =
                            customUserDetailsService
                                    .loadUserByUsername(username);

                    // 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Spring Security에 인증 정보 저장
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println("JWT 인증 성공: " + username);
                    System.out.println(
                            "권한: " + userDetails.getAuthorities()
                    );
                }
            }

        } catch (Exception e) {

            System.out.println("===== JWT 인증 실패 =====");
            System.out.println("요청 URI: " + requestUri);
            System.out.println(
                    "오류 타입: " + e.getClass().getName()
            );
            System.out.println(
                    "오류 내용: " + e.getMessage()
            );

            SecurityContextHolder.clearContext();
        }

        // 다음 필터 진행
        filterChain.doFilter(request, response);
    }
}