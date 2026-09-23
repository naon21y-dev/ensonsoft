package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인 ID
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // BCrypt 암호화 비밀번호 저장
    @Column(nullable = false, length = 255)
    private String password;

    // 사용자 이름
    @Column(nullable = false, length = 50)
    private String name;

    // 이메일
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // 권한: USER / ADMIN
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    // 계정 활성화 여부
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    // 가입일
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Role {
        USER,
        ADMIN
    }
}