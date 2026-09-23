package com.logic.project.config;

import com.logic.project.security.CustomUserDetailsService;
import com.logic.project.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // Vue 개발 서버 CORS
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                // REST API
                .csrf(csrf -> csrf.disable())

                // API 인증 실패 시 JSON 401 반환
                .exceptionHandling(exceptions ->
                        exceptions.defaultAuthenticationEntryPointFor(
                                (request, response, exception) -> {
                                    response.setStatus(401);
                                    response.setContentType(
                                            "application/json;charset=UTF-8"
                                    );

                                    response.getWriter().write(
                                            "{\"status\":401," +
                                                    "\"message\":\"로그인이 만료되었거나 인증 정보가 없습니다. 다시 로그인해주세요.\"}"
                                    );
                                },
                                request ->
                                        request.getRequestURI()
                                                .startsWith("/api/")
                        )
                )

                // 접근 권한
                .authorizeHttpRequests(auth -> auth

                        // 공개
                        .requestMatchers(
                                "/",
                                "/login",
                                "/api/auth/**"
                        ).permitAll()

                        // 관리자
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // 사용자
                        .requestMatchers(
                                "/api/user/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // 게시판
                        .requestMatchers(
                                "/api/boards/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // 통합관제
                        .requestMatchers(
                                "/api/monitoring/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // 현장 관리
                        .requestMatchers(
                                "/api/sites/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // 장비 관리
                        .requestMatchers(
                                "/api/equipments/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // 유지보수 관리
                        .requestMatchers(
                                "/api/maintenance/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // 대시보드
                        .requestMatchers(
                                "/api/dashboard/**",
                                "/api/dashboard"
                        ).hasAnyRole("USER", "ADMIN")

                        // 나머지는 로그인 필요
                        .anyRequest().authenticated()
                )

                // 기존 form login 유지
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                // 로그아웃
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )

                // 사용자 정보 조회
                .userDetailsService(customUserDetailsService)

                // JWT 필터
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // Vue 개발 서버 CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:5175"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}