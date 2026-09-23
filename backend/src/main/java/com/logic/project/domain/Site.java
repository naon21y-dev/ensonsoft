package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "sites",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_sites_code",
                        columnNames = "site_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ========================================
    // 현장 기본 정보
    // ========================================

    // 현장 코드
    // 예: SITE-001
    @Column(
            name = "site_code",
            nullable = false,
            length = 30
    )
    private String siteCode;


    // 현장명
    // 예: 서울TG 스마트톨링 현장
    @Column(
            nullable = false,
            length = 100
    )
    private String name;


    // 현장 주소
    @Column(
            nullable = false,
            length = 200
    )
    private String address;


    // 현장 상세 설명
    @Column(
            length = 500
    )
    private String description;


    // ========================================
    // 현장 유형
    // ========================================

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private SiteType siteType;


    // ========================================
    // 현장 상태
    // ========================================

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    @Builder.Default
    private SiteStatus status =
            SiteStatus.NORMAL;


    // ========================================
    // 담당자 정보
    // ========================================

    @Column(length = 50)
    private String managerName;


    @Column(length = 30)
    private String managerTel;


    // ========================================
    // 등록 / 수정 시간
    // ========================================

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(nullable = false)
    private LocalDateTime updatedAt;


    // ========================================
    // 생성 시 자동 실행
    // ========================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = SiteStatus.NORMAL;
        }
    }


    // ========================================
    // 수정 시 자동 실행
    // ========================================

    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }


    // ========================================
    // 현장 유형
    // ========================================

    public enum SiteType {

        // 요금소
        TOLL_GATE,

        // 터널
        TUNNEL,

        // 교량
        BRIDGE,

        // 도로
        ROAD,

        // 주차장
        PARKING,

        // 기타
        ETC
    }


    // ========================================
    // 현장 상태
    // ========================================

    public enum SiteStatus {

        // 정상 운영
        NORMAL,

        // 주의
        WARNING,

        // 장애
        ERROR,

        // 점검중
        MAINTENANCE,

        // 운영 중지
        INACTIVE
    }
}