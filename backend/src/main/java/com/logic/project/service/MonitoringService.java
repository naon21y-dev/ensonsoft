package com.logic.project.service;

import com.logic.project.domain.EventProcessHistory;
import com.logic.project.domain.VehicleRecognition;
import com.logic.project.domain.VideoEvent;
import com.logic.project.dto.EventProcessHistoryResponse;
import com.logic.project.dto.VehicleRecognitionResponse;
import com.logic.project.dto.VideoEventResponse;
import com.logic.project.repository.EventProcessHistoryRepository;
import com.logic.project.repository.VehicleRecognitionRepository;
import com.logic.project.repository.VideoEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitoringService {

    private final VehicleRecognitionRepository vehicleRecognitionRepository;
    private final VideoEventRepository videoEventRepository;

    // 처리 이력 Repository 추가
    private final EventProcessHistoryRepository eventProcessHistoryRepository;


    // ========================================
    // 차량번호 인식
    // ========================================

    public List<VehicleRecognitionResponse> getVehicleRecognitions() {

        return vehicleRecognitionRepository
                .findAllByOrderByRecognizedAtDesc()
                .stream()
                .map(VehicleRecognitionResponse::from)
                .toList();
    }


    public List<VehicleRecognitionResponse> searchVehicleRecognitions(
            String plateNumber
    ) {

        return vehicleRecognitionRepository
                .findByPlateNumberContainingIgnoreCaseOrderByRecognizedAtDesc(
                        plateNumber
                )
                .stream()
                .map(VehicleRecognitionResponse::from)
                .toList();
    }


    public List<VehicleRecognitionResponse> searchVehicleRecognitionsByLocation(
            String location
    ) {

        return vehicleRecognitionRepository
                .findByLocationContainingIgnoreCaseOrderByRecognizedAtDesc(
                        location
                )
                .stream()
                .map(VehicleRecognitionResponse::from)
                .toList();
    }


    public List<VehicleRecognitionResponse> getVehicleRecognitionsByStatus(
            VehicleRecognition.RecognitionStatus status
    ) {

        return vehicleRecognitionRepository
                .findByStatusOrderByRecognizedAtDesc(status)
                .stream()
                .map(VehicleRecognitionResponse::from)
                .toList();
    }


    // ========================================
    // 영상 이벤트
    // ========================================

    public List<VideoEventResponse> getVideoEvents() {

        return videoEventRepository
                .findAllByOrderByOccurredAtDesc()
                .stream()
                .map(VideoEventResponse::from)
                .toList();
    }


    public List<VideoEventResponse> getVideoEventsByStatus(
            VideoEvent.EventStatus status
    ) {

        return videoEventRepository
                .findByStatusOrderByOccurredAtDesc(status)
                .stream()
                .map(VideoEventResponse::from)
                .toList();
    }


    public List<VideoEventResponse> getVideoEventsByType(
            VideoEvent.EventType eventType
    ) {

        return videoEventRepository
                .findByEventTypeOrderByOccurredAtDesc(eventType)
                .stream()
                .map(VideoEventResponse::from)
                .toList();
    }


    public List<VideoEventResponse> getVideoEventsBySeverity(
            VideoEvent.Severity severity
    ) {

        return videoEventRepository
                .findBySeverityOrderByOccurredAtDesc(severity)
                .stream()
                .map(VideoEventResponse::from)
                .toList();
    }


    public List<VideoEventResponse> getVideoEventsByLocation(
            String location
    ) {

        return videoEventRepository
                .findByLocationContainingIgnoreCaseOrderByOccurredAtDesc(
                        location
                )
                .stream()
                .map(VideoEventResponse::from)
                .toList();
    }


    // ========================================
    // 이벤트 상세 조회
    // ========================================

    @Transactional
    public VideoEventResponse getVideoEvent(Long id) {

        VideoEvent event = findVideoEvent(id);

        return VideoEventResponse.from(event);
    }


    // ========================================
    // 이벤트 처리 이력 조회
    // ========================================

    public List<EventProcessHistoryResponse> getEventProcessHistories(
            Long eventId
    ) {

        // 존재하지 않는 이벤트 ID인지 먼저 확인
        findVideoEvent(eventId);

        return eventProcessHistoryRepository
                .findByVideoEventIdOrderByProcessedAtDesc(eventId)
                .stream()
                .map(EventProcessHistoryResponse::from)
                .toList();
    }


    // ========================================
    // 이벤트 처리 시작
    // ========================================

    @Transactional
    public VideoEventResponse startProcessing(
            Long id,
            String processor
    ) {

        VideoEvent event = findVideoEvent(id);

        // 이벤트 상태 변경
        event.setStatus(
                VideoEvent.EventStatus.PROCESSING
        );

        event.setProcessor(processor);

        // 처리 이력 저장
        EventProcessHistory history =
                EventProcessHistory.builder()
                        .videoEvent(event)
                        .status(
                                VideoEvent.EventStatus.PROCESSING
                        )
                        .processor(processor)
                        .description("이벤트 처리 시작")
                        .processedAt(LocalDateTime.now())
                        .build();

        eventProcessHistoryRepository.save(history);

        return VideoEventResponse.from(event);
    }


    // ========================================
    // 이벤트 처리 완료
    // ========================================

    @Transactional
    public VideoEventResponse completeProcessing(
            Long id,
            String processor
    ) {

        VideoEvent event = findVideoEvent(id);

        // 이벤트 상태 변경
        event.setStatus(
                VideoEvent.EventStatus.COMPLETED
        );

        event.setProcessor(processor);

        event.setProcessedAt(
                LocalDateTime.now()
        );

        // 처리 이력 저장
        EventProcessHistory history =
                EventProcessHistory.builder()
                        .videoEvent(event)
                        .status(
                                VideoEvent.EventStatus.COMPLETED
                        )
                        .processor(processor)
                        .description("이벤트 처리 완료")
                        .processedAt(LocalDateTime.now())
                        .build();

        eventProcessHistoryRepository.save(history);

        return VideoEventResponse.from(event);
    }


    // ========================================
    // 이벤트 조회 공통 메서드
    // ========================================

    private VideoEvent findVideoEvent(Long id) {

        return videoEventRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "영상 이벤트를 찾을 수 없습니다."
                                )
                );
    }
}