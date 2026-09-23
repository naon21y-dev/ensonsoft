package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenances", uniqueConstraints = @UniqueConstraint(
        name = "uk_maintenances_code", columnNames = "maintenance_code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maintenance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "maintenance_code", nullable = false, length = 40, updatable = false)
    private String maintenanceCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false, length = 2000)
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MaintenanceStatus status = MaintenanceStatus.REPORTED;
    @Column(nullable = false)
    private LocalDateTime occurredAt;
    @Column(nullable = false, length = 50, updatable = false)
    private String reportedBy;
    @Column(length = 2000)
    private String result;
    @Column(nullable = false, updatable = false)
    private LocalDateTime reportedAt;
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (reportedAt == null) reportedAt = LocalDateTime.now();
        if (status == null) status = MaintenanceStatus.REPORTED;
    }

    public enum MaintenanceStatus { REPORTED, IN_PROGRESS, COMPLETED }
}
