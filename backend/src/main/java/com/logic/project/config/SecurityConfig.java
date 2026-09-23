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
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                .csrf(csrf -> csrf.disable())

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

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/login",
                                "/api/auth/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/api/user/**"
                        ).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(
                                "/api/boards/**"
                        ).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(
                                "/api/monitoring/**"
                        ).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(
                                "/api/sites/**"
                        ).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(
                                "/api/equipments/**"
                        ).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(
                                "/api/maintenance/**"
                        ).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(
                                "/api/dashboard/**",
                                "/api/dashboard"
                        ).hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )

                .userDetailsService(customUserDetailsService)

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:5175",
                        "https://ensonsoft-web.onrender.com"
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}
