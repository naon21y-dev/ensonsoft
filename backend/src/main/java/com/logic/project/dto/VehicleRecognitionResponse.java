package com.logic.project.dto;

import com.logic.project.domain.VehicleRecognition;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VehicleRecognitionResponse {

    private Long id;

    // 차량번호
    private String plateNumber;

    // 인식 위치
    private String location;

    // 인식 시간
    private LocalDateTime recognizedAt;

    // 인식 상태
    private VehicleRecognition.RecognitionStatus status;

    // 카메라 ID
    private String cameraId;

    // 인식 정확도
    private Double confidence;

    // 데이터 생성 시간
    private LocalDateTime createdAt;

    // Entity -> Response DTO 변환
    public static VehicleRecognitionResponse from(
            VehicleRecognition vehicleRecognition
    ) {

        return VehicleRecognitionResponse.builder()
                .id(vehicleRecognition.getId())
                .plateNumber(vehicleRecognition.getPlateNumber())
                .location(vehicleRecognition.getLocation())
                .recognizedAt(vehicleRecognition.getRecognizedAt())
                .status(vehicleRecognition.getStatus())
                .cameraId(vehicleRecognition.getCameraId())
                .confidence(vehicleRecognition.getConfidence())
                .createdAt(vehicleRecognition.getCreatedAt())
                .build();
    }
}