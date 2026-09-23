package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_recognitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRecognition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 차량번호
    @Column(nullable = false, length = 20)
    private String plateNumber;

    // 차량번호가 인식된 위치
    @Column(nullable = false, length = 100)
    private String location;

    // 인식 시간
    @Column(nullable = false)
    private LocalDateTime recognizedAt;

    // 인식 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RecognitionStatus status = RecognitionStatus.NORMAL;

    // 카메라/장비 식별값
    @Column(length = 50)
    private String cameraId;

    // 인식 정확도
    // 예: 98.7
    @Column
    private Double confidence;

    // 데이터 생성 시간
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (recognizedAt == null) {
            recognizedAt = now;
        }

        createdAt = now;
    }

    public enum RecognitionStatus {

        // 정상 인식
        NORMAL,

        // 확인 필요
        WARNING,

        // 인식 오류
        ERROR
    }
}