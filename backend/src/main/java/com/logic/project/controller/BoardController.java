package com.logic.project.controller;

import com.logic.project.dto.BoardRequest;
import com.logic.project.dto.BoardResponse;
import com.logic.project.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시글 목록 + 검색
    @GetMapping
    public ResponseEntity<List<BoardResponse>> getBoards(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String writer
    ) {

        return ResponseEntity.ok(
                boardService.getBoards(keyword, writer)
        );
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                boardService.getBoard(id)
        );
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(
            @Valid @RequestBody BoardRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        BoardResponse response =
                boardService.createBoard(request, username);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();
        boolean isAdmin = isAdmin(authentication);

        return ResponseEntity.ok(
                boardService.updateBoard(
                        id,
                        request,
                        username,
                        isAdmin
                )
        );
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String username = authentication.getName();
        boolean isAdmin = isAdmin(authentication);

        boardService.deleteBoard(
                id,
                username,
                isAdmin
        );

        return ResponseEntity.noContent().build();
    }

    // ADMIN 권한 확인
    private boolean isAdmin(Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );
    }
}