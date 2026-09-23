package com.logic.project.security;

import com.logic.project.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    // 권한 정보
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + member.getRole().name())
        );
    }

    // 비밀번호
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    // 로그인 아이디
    @Override
    public String getUsername() {
        return member.getUsername();
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return member.isEnabled();
    }

    // 필요할 때 Member 자체를 가져오기 위한 메서드
    public Member getMember() {
        return member;
    }
}