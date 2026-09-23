package com.logic.project.dto;

import com.logic.project.domain.VideoEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VideoEventResponse {

    private Long id;

    // 이벤트 종류
    private VideoEvent.EventType eventType;

    // 발생 위치
    private String location;

    // 카메라 ID
    private String cameraId;

    // 이벤트 발생 시간
    private LocalDateTime occurredAt;

    // 처리 상태
    private VideoEvent.EventStatus status;

    // 위험도
    private VideoEvent.Severity severity;

    // 상세 내용
    private String description;

    // 담당 처리자
    private String processor;

    // 처리 완료 시간
    private LocalDateTime processedAt;

    // 데이터 생성 시간
    private LocalDateTime createdAt;

    // Entity -> Response DTO 변환
    public static VideoEventResponse from(
            VideoEvent videoEvent
    ) {

        return VideoEventResponse.builder()
                .id(videoEvent.getId())
                .eventType(videoEvent.getEventType())
                .location(videoEvent.getLocation())
                .cameraId(videoEvent.getCameraId())
                .occurredAt(videoEvent.getOccurredAt())
                .status(videoEvent.getStatus())
                .severity(videoEvent.getSeverity())
                .description(videoEvent.getDescription())
                .processor(videoEvent.getProcessor())
                .processedAt(videoEvent.getProcessedAt())
                .createdAt(videoEvent.getCreatedAt())
                .build();
    }
}