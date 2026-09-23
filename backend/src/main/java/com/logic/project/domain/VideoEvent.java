package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이벤트 종류
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventType eventType;

    // 이벤트 발생 위치
    @Column(nullable = false, length = 100)
    private String location;

    // 이벤트를 감지한 카메라
    @Column(nullable = false, length = 50)
    private String cameraId;

    // 이벤트 발생 시간
    @Column(nullable = false)
    private LocalDateTime occurredAt;

    // 이벤트 처리 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EventStatus status = EventStatus.UNPROCESSED;

    // 위험도
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Severity severity = Severity.NORMAL;

    // 이벤트 상세 내용
    @Column(length = 500)
    private String description;

    // 담당 처리자
    @Column(length = 50)
    private String processor;

    // 처리 완료 시간
    private LocalDateTime processedAt;

    // 데이터 생성 시간
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (occurredAt == null) {
            occurredAt = now;
        }

        createdAt = now;
    }

    // 영상분석 이벤트 종류
    public enum EventType {

        // 사고 감지
        ACCIDENT,

        // 정지 차량
        STOPPED_VEHICLE,

        // 역주행
        WRONG_WAY,

        // 보행자 감지
        PEDESTRIAN,

        // 혼잡 감지
        CONGESTION,

        // 기타
        ETC
    }

    // 처리 상태
    public enum EventStatus {

        // 미처리
        UNPROCESSED,

        // 처리중
        PROCESSING,

        // 처리완료
        COMPLETED
    }

    // 위험도
    public enum Severity {

        // 일반
        NORMAL,

        // 주의
        WARNING,

        // 긴급
        CRITICAL
    }
}