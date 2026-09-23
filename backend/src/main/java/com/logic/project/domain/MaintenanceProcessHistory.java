package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_process_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceProcessHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_id", nullable = false)
    private Maintenance maintenance;
    // 최초 접수 이력의 이전 상태는 null입니다.
    @Enumerated(EnumType.STRING) @Column(length = 20)
    private Maintenance.MaintenanceStatus previousStatus;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Maintenance.MaintenanceStatus newStatus;
    @Column(nullable = false, length = 50)
    private String processedBy;
    @Column(nullable = false, length = 2000)
    private String description;
    @Column(nullable = false, updatable = false)
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        if (processedAt == null) processedAt = LocalDateTime.now();
    }
}
