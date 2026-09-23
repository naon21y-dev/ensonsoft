package com.logic.project.controller;

import com.logic.project.dto.MemberResponse;
import com.logic.project.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final MemberService memberService;

    // 로그인한 사용자 본인 정보 조회
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo(
            Authentication authentication
    ) {

        String username = authentication.getName();

        MemberResponse member =
                memberService.getMember(username);

        return ResponseEntity.ok(member);
    }
}