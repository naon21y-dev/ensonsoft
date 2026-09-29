package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "attendances", uniqueConstraints = @UniqueConstraint(name = "uk_attendance_member_date", columnNames = {"member_id", "work_date"}),
        indexes = @Index(name = "idx_attendance_work_date", columnList = "work_date"))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Attendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;
    @Column(nullable = false)
    private Instant checkInAt;
    private Instant checkOutAt;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private WorkType workType;
    @Column(length = 2000)
    private String notes;

    public enum WorkType { NORMAL, DUTY, EMERGENCY, SUBSTITUTE }
    public enum Status { NOT_CHECKED_IN, WORKING, CHECKED_OUT }
}
