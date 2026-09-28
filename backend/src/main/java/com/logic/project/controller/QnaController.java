package com.logic.project.controller;

import com.logic.project.domain.Qna;
import com.logic.project.dto.QnaAnswerRequest;
import com.logic.project.dto.QnaRequest;
import com.logic.project.dto.QnaResponse;
import com.logic.project.service.QnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    // ========================================
    // 전체 문의 조회
    // ========================================

    @GetMapping
    public ResponseEntity<List<QnaResponse>> findAll() {

        return ResponseEntity.ok(
                qnaService.findAll()
        );
    }

    // ========================================
    // 내 문의 조회
    // ========================================

    @GetMapping("/my")
    public ResponseEntity<List<QnaResponse>> findMyQna(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                qnaService.findMyQna(
                        authentication.getName()
                )
        );
    }

    // ========================================
    // 상태별 조회
    // ========================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<QnaResponse>> findByStatus(
            @PathVariable Qna.Status status
    ) {

        return ResponseEntity.ok(
                qnaService.findByStatus(status)
        );
    }

    // ========================================
    // 유형별 조회
    // ========================================

    @GetMapping("/category/{category}")
    public ResponseEntity<List<QnaResponse>> findByCategory(
            @PathVariable Qna.Category category
    ) {

        return ResponseEntity.ok(
                qnaService.findByCategory(category)
        );
    }

    // ========================================
    // 검색
    // ========================================

    @GetMapping("/search")
    public ResponseEntity<List<QnaResponse>> search(
            @RequestParam String keyword
    ) {

        return ResponseEntity.ok(
                qnaService.search(keyword)
        );
    }

    // ========================================
    // 상세 조회
    // ========================================

    @GetMapping("/{id}")
    public ResponseEntity<QnaResponse> findById(
            @PathVariable Long id,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                qnaService.findById(
                        id,
                        authentication.getName(),
                        isAdmin(authentication)
                )
        );
    }

    // ========================================
    // 문의 등록
    // ========================================

    @PostMapping
    public ResponseEntity<QnaResponse> create(
            @Valid @RequestBody QnaRequest request,
            Authentication authentication
    ) {

        QnaResponse response =
                qnaService.create(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ========================================
    // 문의 수정
    // ========================================

    @PutMapping("/{id}")
    public ResponseEntity<QnaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody QnaRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                qnaService.update(
                        id,
                        request,
                        authentication.getName(),
                        isAdmin(authentication)
                )
        );
    }

    // ========================================
    // 문의 삭제
    // ========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication
    ) {

        qnaService.delete(
                id,
                authentication.getName(),
                isAdmin(authentication)
        );

        return ResponseEntity.noContent().build();
    }

    // ========================================
    // 관리자 답변 등록 / 수정
    // ========================================

    @PostMapping("/{id}/answer")
    public ResponseEntity<QnaResponse> answer(
            @PathVariable Long id,
            @Valid @RequestBody QnaAnswerRequest request,
            Authentication authentication
    ) {

        checkAdmin(authentication);

        return ResponseEntity.ok(
                qnaService.answer(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }

    // ========================================
    // 관리자 답변 삭제
    // ========================================

    @DeleteMapping("/{id}/answer")
    public ResponseEntity<QnaResponse> deleteAnswer(
            @PathVariable Long id,
            Authentication authentication
    ) {

        checkAdmin(authentication);

        return ResponseEntity.ok(
                qnaService.deleteAnswer(id)
        );
    }

    // ========================================
    // ADMIN 확인
    // ========================================

    private boolean isAdmin(
            Authentication authentication
    ) {

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority
                                .getAuthority()
                                .equals("ROLE_ADMIN")
                );
    }

    private void checkAdmin(
            Authentication authentication
    ) {

        if (!isAdmin(authentication)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "관리자만 답변을 등록할 수 있습니다."
            );
        }
    }
}
