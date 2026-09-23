package com.logic.project.service;

import com.logic.project.config.JwtConfig;
import com.logic.project.domain.Member;
import com.logic.project.dto.LoginRequest;
import com.logic.project.dto.MemberResponse;
import com.logic.project.dto.SignupRequest;
import com.logic.project.dto.TokenResponse;
import com.logic.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;

    // 회원가입
    @Transactional
    public MemberResponse signup(SignupRequest request) {

        // 아이디 중복 검사
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 회원 생성
        Member member = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .role(Member.Role.USER)
                .enabled(true)
                .build();

        // DB 저장
        Member savedMember = memberRepository.save(member);

        return MemberResponse.from(savedMember);
    }

    // 로그인
    public TokenResponse login(LoginRequest request) {

        // Spring Security 로그인 인증
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 인증 성공 후 회원 조회
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        // JWT 생성
        String token = jwtConfig.generateToken(
                member.getUsername(),
                member.getRole().name()
        );

        // Vue에 JWT 반환
        return new TokenResponse(
                token,
                "Bearer",
                member.getUsername(),
                member.getRole().name()
        );
    }
}