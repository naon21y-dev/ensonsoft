package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_status_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ========================================
    // 어떤 장비의 이력인지
    // ========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "equipment_id",
            nullable = false
    )
    private Equipment equipment;


    // ========================================
    // 변경 전 상태
    // ========================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "previous_status",
            nullable = false,
            length = 20
    )
    private Equipment.EquipmentStatus previousStatus;


    // ========================================
    // 변경 후 상태
    // ========================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "new_status",
            nullable = false,
            length = 20
    )
    private Equipment.EquipmentStatus newStatus;


    // ========================================
    // 상태 변경 담당자
    // ========================================

    @Column(
            nullable = false,
            length = 255
    )
    private String changedBy;


    // ========================================
    // 변경 사유
    // ========================================

    @Column(length = 2550)
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