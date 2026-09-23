package com.logic.project.controller;

import com.logic.project.domain.VideoEvent;
import com.logic.project.domain.VehicleRecognition;
import com.logic.project.dto.EventProcessHistoryResponse;
import com.logic.project.dto.VideoEventResponse;
import com.logic.project.dto.VehicleRecognitionResponse;
import com.logic.project.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;


    // =========================================================
    // 차량번호 인식
    // =========================================================

    // 전체 차량번호 인식 조회
    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleRecognitionResponse>>
    getVehicleRecognitions() {

        return ResponseEntity.ok(
                monitoringService.getVehicleRecognitions()
        );
    }


    // 차량번호 검색
    @GetMapping("/vehicles/search")
    public ResponseEntity<List<VehicleRecognitionResponse>>
    searchVehicleRecognitions(
            @RequestParam String plateNumber
    ) {

        return ResponseEntity.ok(
                monitoringService.searchVehicleRecognitions(
                        plateNumber
                )
        );
    }


    // 위치 검색
    @GetMapping("/vehicles/location")
    public ResponseEntity<List<VehicleRecognitionResponse>>
    searchVehicleRecognitionsByLocation(
            @RequestParam String location
    ) {

        return ResponseEntity.ok(
                monitoringService.searchVehicleRecognitionsByLocation(
                        location
                )
        );
    }


    // 차량번호 인식 상태별 조회
    @GetMapping("/vehicles/status/{status}")
    public ResponseEntity<List<VehicleRecognitionResponse>>
    getVehicleRecognitionsByStatus(
            @PathVariable VehicleRecognition.RecognitionStatus status
    ) {

        return ResponseEntity.ok(
                monitoringService.getVehicleRecognitionsByStatus(
                        status
                )
        );
    }


    // =========================================================
    // 영상분석 이벤트
    // =========================================================

    // 전체 이벤트 조회
    @GetMapping("/events")
    public ResponseEntity<List<VideoEventResponse>>
    getVideoEvents() {

        return ResponseEntity.ok(
                monitoringService.getVideoEvents()
        );
    }


    // 이벤트 상세 조회
    @GetMapping("/events/{id}")
    public ResponseEntity<VideoEventResponse>
    getVideoEvent(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                monitoringService.getVideoEvent(id)
        );
    }


    // 처리 상태별 조회
    @GetMapping("/events/status/{status}")
    public ResponseEntity<List<VideoEventResponse>>
    getVideoEventsByStatus(
            @PathVariable VideoEvent.EventStatus status
    ) {

        return ResponseEntity.ok(
                monitoringService.getVideoEventsByStatus(
                        status
                )
        );
    }


    // 이벤트 종류별 조회
    @GetMapping("/events/type/{eventType}")
    public ResponseEntity<List<VideoEventResponse>>
    getVideoEventsByType(
            @PathVariable VideoEvent.EventType eventType
    ) {

        return ResponseEntity.ok(
                monitoringService.getVideoEventsByType(
                        eventType
                )
        );
    }


    // 위험도별 조회
    @GetMapping("/events/severity/{severity}")
    public ResponseEntity<List<VideoEventResponse>>
    getVideoEventsBySeverity(
            @PathVariable VideoEvent.Severity severity
    ) {

        return ResponseEntity.ok(
                monitoringService.getVideoEventsBySeverity(
                        severity
                )
        );
    }


    // 위치별 조회
    @GetMapping("/events/location")
    public ResponseEntity<List<VideoEventResponse>>
    getVideoEventsByLocation(
            @RequestParam String location
    ) {

        return ResponseEntity.ok(
                monitoringService.getVideoEventsByLocation(
                        location
                )
        );
    }


    // =========================================================
    // 이벤트 처리 이력
    // =========================================================

    // 특정 이벤트 처리 이력 조회
    @GetMapping("/events/{id}/histories")
    public ResponseEntity<List<EventProcessHistoryResponse>>
    getEventProcessHistories(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                monitoringService.getEventProcessHistories(id)
        );
    }


    // =========================================================
    // 이벤트 처리
    // =========================================================

    // 이벤트 처리 시작
    @PutMapping("/events/{id}/processing")
    public ResponseEntity<VideoEventResponse>
    startProcessing(
            @PathVariable Long id,
            Authentication authentication
    ) {

        // 현재 JWT로 로그인한 사용자 ID
        String processor = authentication.getName();

        return ResponseEntity.ok(
                monitoringService.startProcessing(
                        id,
                        processor
                )
        );
    }


    // 이벤트 처리 완료
    @PutMapping("/events/{id}/complete")
    public ResponseEntity<VideoEventResponse>
    completeProcessing(
            @PathVariable Long id,
            Authentication authentication
    ) {

        // 현재 JWT로 로그인한 사용자 ID
        String processor = authentication.getName();

        return ResponseEntity.ok(
                monitoringService.completeProcessing(
                        id,
                        processor
                )
        );
    }
}