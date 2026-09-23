package com.logic.project.controller;

import com.logic.project.dto.LoginRequest;
import com.logic.project.dto.MemberResponse;
import com.logic.project.dto.SignupRequest;
import com.logic.project.dto.TokenResponse;
import com.logic.project.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {

        MemberResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        TokenResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}