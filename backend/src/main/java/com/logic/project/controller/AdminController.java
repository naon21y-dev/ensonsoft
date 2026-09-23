package com.logic.project.controller;

import com.logic.project.dto.MemberResponse;
import com.logic.project.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    // 전체 회원 조회
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {

        List<MemberResponse> members = memberService.getAllMembers();

        return ResponseEntity.ok(members);
    }

    // 특정 회원 조회
    @GetMapping("/members/{id}")
    public ResponseEntity<MemberResponse> getMember(
            @PathVariable Long id
    ) {

        MemberResponse member = memberService.getMemberById(id);

        return ResponseEntity.ok(member);
    }
}