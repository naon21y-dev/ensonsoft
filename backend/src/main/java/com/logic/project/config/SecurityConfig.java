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
                // ========================================
                // CORS
                // ========================================
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                // ========================================
                // CSRF 비활성화
                // JWT 방식이므로 비활성화
                // ========================================
                .csrf(csrf -> csrf.disable())

                // ========================================
                // 인증 실패 처리
                // ========================================
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

                // ========================================
                // API 접근 권한
                // ========================================
                .authorizeHttpRequests(auth -> auth

                        // --------------------------------
                        // 로그인 없이 접근 가능
                        // --------------------------------
                        .requestMatchers(
                                "/",
                                "/login",
                                "/api/auth/**"
                        ).permitAll()

                        // --------------------------------
                        // 관리자 전용
                        // --------------------------------
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // --------------------------------
                        // 사용자 API
                        // --------------------------------
                        .requestMatchers(
                                "/api/user/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // --------------------------------
                        // 공지사항
                        // --------------------------------
                        .requestMatchers(
                                "/api/boards/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // --------------------------------
                        // 통합관제
                        // --------------------------------
                        .requestMatchers(
                                "/api/monitoring/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // --------------------------------
                        // 현장 관리
                        // --------------------------------
                        .requestMatchers(
                                "/api/sites/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // --------------------------------
                        // 장비 관리
                        // --------------------------------
                        .requestMatchers(
                                "/api/equipments/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // --------------------------------
                        // 유지보수
                        // --------------------------------
                        .requestMatchers(
                                "/api/maintenance/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // ========================================
                        // Q&A
                        // USER / ADMIN 접근 가능
                        // ========================================
                        .requestMatchers(
                                "/api/qna/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // --------------------------------
                        // 대시보드
                        // --------------------------------
                        .requestMatchers(
                                "/api/dashboard/**",
                                "/api/dashboard"
                        ).hasAnyRole("USER", "ADMIN")

                        // --------------------------------
                        // 그 외 모든 요청
                        // --------------------------------
                        .anyRequest().authenticated()
                )

                // ========================================
                // Form Login
                // ========================================
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                // ========================================
                // Logout
                // ========================================
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )

                // ========================================
                // UserDetailsService
                // ========================================
                .userDetailsService(customUserDetailsService)

                // ========================================
                // JWT Filter
                // ========================================
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // ========================================
    // CORS 설정
    // ========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // ========================================
        // 접근 허용 Frontend 주소
        // ========================================

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:5175",
                        "https://ensonsoft-web.onrender.com"
                )
        );

        // ========================================
        // 허용 HTTP Method
        // ========================================

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

        // ========================================
        // 모든 Header 허용
        // ========================================

        configuration.setAllowedHeaders(
                List.of("*")
        );

        // ========================================
        // Frontend에서 Authorization Header 접근
        // ========================================

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

    // ========================================
    // Password Encoder
    // ========================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // ========================================
    // Authentication Manager
    // ========================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}
