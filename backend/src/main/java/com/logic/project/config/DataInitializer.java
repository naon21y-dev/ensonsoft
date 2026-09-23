package com.logic.project.config;

import com.logic.project.domain.Member;
import com.logic.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // 일반 사용자 생성
        if (!memberRepository.existsByUsername("user")) {

            Member user = Member.builder()
                    .username("user")
                    .password(passwordEncoder.encode("1234"))
                    .name("일반사용자")
                    .email("user@test.com")
                    .role(Member.Role.USER)
                    .enabled(true)
                    .build();

            memberRepository.save(user);
        }

        // 관리자 생성
        if (!memberRepository.existsByUsername("admin")) {

            Member admin = Member.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("1234"))
                    .name("관리자")
                    .email("admin@test.com")
                    .role(Member.Role.ADMIN)
                    .enabled(true)
                    .build();

            memberRepository.save(admin);
        }

        System.out.println("===== 초기 사용자 데이터 확인 완료 =====");
    }
}