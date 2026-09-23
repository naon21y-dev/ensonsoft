package com.logic.project.dto;

import com.logic.project.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .email(member.getEmail())
                .role(member.getRole().name())
                .enabled(member.isEnabled())
                .createdAt(member.getCreatedAt())
                .build();
    }
}