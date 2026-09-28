package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================
    // 문의 유형
    // ========================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Category category;

    // ========================================
    // 제목 / 내용
    // ========================================

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // ========================================
    // 작성자
    // ========================================

    @Column(nullable = false, length = 50)
    private String username;

    // ========================================
    // 비밀글
    // ========================================

    @Builder.Default
    @Column(nullable = false)
    private boolean secret = false;

    // ========================================
    // 답변 상태
    // ========================================

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private Status status = Status.WAITING;

    // ========================================
    // 관리자 답변
    // ========================================

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(length = 50)
    private String answeredBy;

    private LocalDateTime answeredAt;

    // ========================================
    // 작성 / 수정 시간
    // ========================================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = Status.WAITING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ========================================
    // ENUM
    // ========================================

    public enum Category {
        GENERAL,        // 일반 문의
        SITE,           // 현장 문의
        EQUIPMENT,      // 장비 문의
        VEHICLE,        // 차량번호 인식
        VIDEO_ANALYSIS, // 영상분석
        MAINTENANCE,    // 유지보수
        ETC             // 기타
    }

    public enum Status {
        WAITING,        // 답변 대기
        ANSWERED        // 답변 완료
    }
}
