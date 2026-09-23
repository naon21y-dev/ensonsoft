package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "site_status_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ========================================
    // 어떤 현장의 이력인지
    // ========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "site_id",
            nullable = false
    )
    private Site site;


    // ========================================
    // 변경 전 상태
    // ========================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "previous_status",
            nullable = false,
            length = 20
    )
    private Site.SiteStatus previousStatus;


    // ========================================
    // 변경 후 상태
    // ========================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "new_status",
            nullable = false,
            length = 20
    )
    private Site.SiteStatus newStatus;


    // ========================================
    // 상태 변경 담당자
    // ========================================

    @Column(
            nullable = false,
            length = 50
    )
    private String changedBy;


    // ========================================
    // 변경 사유
    // ========================================

    @Column(length = 500)
    private String reason;


    // ========================================
    // 변경 시간
    // ========================================

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime changedAt;


    @PrePersist
    protected void onCreate() {

        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }
}