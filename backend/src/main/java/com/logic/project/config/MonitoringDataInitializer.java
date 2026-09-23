package com.logic.project.config;

import com.logic.project.domain.VehicleRecognition;
import com.logic.project.domain.VideoEvent;
import com.logic.project.repository.VehicleRecognitionRepository;
import com.logic.project.repository.VideoEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonitoringDataInitializer implements CommandLineRunner {

    private final VehicleRecognitionRepository vehicleRecognitionRepository;
    private final VideoEventRepository videoEventRepository;

    @Override
    public void run(String... args) {

        // 이미 데이터가 있으면 다시 넣지 않음
        if (vehicleRecognitionRepository.count() == 0) {
            createVehicleData();
        }

        if (videoEventRepository.count() == 0) {
            createVideoEventData();
        }

        System.out.println("===== 통합관제 더미 데이터 확인 완료 =====");
    }

    // =========================================================
    // 차량번호 인식 더미 데이터
    // =========================================================

    private void createVehicleData() {

        LocalDateTime now = LocalDateTime.now();

        List<VehicleRecognition> vehicles = List.of(

                VehicleRecognition.builder()
                        .plateNumber("12가3456")
                        .location("서울TG 진입차로")
                        .recognizedAt(now.minusMinutes(5))
                        .status(VehicleRecognition.RecognitionStatus.NORMAL)
                        .cameraId("CAM-001")
                        .confidence(98.7)
                        .build(),

                VehicleRecognition.builder()
                        .plateNumber("34나5678")
                        .location("판교TG 2차로")
                        .recognizedAt(now.minusMinutes(12))
                        .status(VehicleRecognition.RecognitionStatus.NORMAL)
                        .cameraId("CAM-002")
                        .confidence(97.4)
                        .build(),

                VehicleRecognition.builder()
                        .plateNumber("56다7890")
                        .location("하남JC 진입부")
                        .recognizedAt(now.minusMinutes(18))
                        .status(VehicleRecognition.RecognitionStatus.WARNING)
                        .cameraId("CAM-003")
                        .confidence(91.8)
                        .build(),

                VehicleRecognition.builder()
                        .plateNumber("78라1234")
                        .location("성남IC 출구")
                        .recognizedAt(now.minusMinutes(25))
                        .status(VehicleRecognition.RecognitionStatus.NORMAL)
                        .cameraId("CAM-004")
                        .confidence(99.2)
                        .build(),

                VehicleRecognition.builder()
                        .plateNumber("90마4567")
                        .location("수원IC 진입부")
                        .recognizedAt(now.minusMinutes(31))
                        .status(VehicleRecognition.RecognitionStatus.WARNING)
                        .cameraId("CAM-005")
                        .confidence(89.6)
                        .build(),

                VehicleRecognition.builder()
                        .plateNumber("11바8901")
                        .location("용인JC 1차로")
                        .recognizedAt(now.minusMinutes(40))
                        .status(VehicleRecognition.RecognitionStatus.NORMAL)
                        .cameraId("CAM-006")
                        .confidence(98.1)
                        .build(),

                VehicleRecognition.builder()
                        .plateNumber("22사2345")
                        .location("서울외곽순환도로")
                        .recognizedAt(now.minusMinutes(48))
                        .status(VehicleRecognition.RecognitionStatus.ERROR)
                        .cameraId("CAM-007")
                        .confidence(62.3)
                        .build(),

                VehicleRecognition.builder()
                        .plateNumber("33아6789")
                        .location("동탄IC 진입부")
                        .recognizedAt(now.minusMinutes(55))
                        .status(VehicleRecognition.RecognitionStatus.NORMAL)
                        .cameraId("CAM-008")
                        .confidence(96.5)
                        .build()
        );

        vehicleRecognitionRepository.saveAll(vehicles);
    }

    // =========================================================
    // 영상분석 이벤트 더미 데이터
    // =========================================================

    private void createVideoEventData() {

        LocalDateTime now = LocalDateTime.now();

        List<VideoEvent> events = List.of(

                VideoEvent.builder()
                        .eventType(VideoEvent.EventType.ACCIDENT)
                        .location("서울TG 1차로")
                        .cameraId("CAM-001")
                        .occurredAt(now.minusMinutes(3))
                        .status(VideoEvent.EventStatus.UNPROCESSED)
                        .severity(VideoEvent.Severity.CRITICAL)
                        .description("차량 사고 의심 이벤트가 감지되었습니다.")
                        .build(),

                VideoEvent.builder()
                        .eventType(VideoEvent.EventType.STOPPED_VEHICLE)
                        .location("판교TG 3차로")
                        .cameraId("CAM-002")
                        .occurredAt(now.minusMinutes(10))
                        .status(VideoEvent.EventStatus.PROCESSING)
                        .severity(VideoEvent.Severity.WARNING)
                        .description("차량이 장시간 정차한 것으로 감지되었습니다.")
                        .processor("admin")
                        .build(),

                VideoEvent.builder()
                        .eventType(VideoEvent.EventType.WRONG_WAY)
                        .location("하남JC 진입부")
                        .cameraId("CAM-003")
                        .occurredAt(now.minusMinutes(16))
                        .status(VideoEvent.EventStatus.UNPROCESSED)
                        .severity(VideoEvent.Severity.CRITICAL)
                        .description("역주행 의심 차량이 감지되었습니다.")
                        .build(),

                VideoEvent.builder()
                        .eventType(VideoEvent.EventType.PEDESTRIAN)
                        .location("성남IC 출구")
                        .cameraId("CAM-004")
                        .occurredAt(now.minusMinutes(22))
                        .status(VideoEvent.EventStatus.COMPLETED)
                        .severity(VideoEvent.Severity.WARNING)
                        .description("차량 통행 구간에서 보행자가 감지되었습니다.")
                        .processor("admin")
                        .processedAt(now.minusMinutes(15))
                        .build(),

                VideoEvent.builder()
                        .eventType(VideoEvent.EventType.CONGESTION)
                        .location("수원IC 진입부")
                        .cameraId("CAM-005")
                        .occurredAt(now.minusMinutes(30))
                        .status(VideoEvent.EventStatus.PROCESSING)
                        .severity(VideoEvent.Severity.WARNING)
                        .description("교통량 증가로 인한 혼잡이 감지되었습니다.")
                        .processor("admin")
                        .build(),

                VideoEvent.builder()
                        .eventType(VideoEvent.EventType.ACCIDENT)
                        .location("용인JC 2차로")
                        .cameraId("CAM-006")
                        .occurredAt(now.minusMinutes(37))
                        .status(VideoEvent.EventStatus.COMPLETED)
                        .severity(VideoEvent.Severity.CRITICAL)
                        .description("접촉 사고 의심 이벤트가 감지되었습니다.")
                        .processor("admin")
                        .processedAt(now.minusMinutes(28))
                        .build(),

                VideoEvent.builder()
                        .eventType(VideoEvent.EventType.STOPPED_VEHICLE)
                        .location("동탄IC 진입부")
                        .cameraId("CAM-008")
                        .occurredAt(now.minusMinutes(45))
                        .status(VideoEvent.EventStatus.UNPROCESSED)
                        .severity(VideoEvent.Severity.NORMAL)
                        .description("갓길 정차 차량이 감지되었습니다.")
                        .build(),

                VideoEvent.builder()
                        .eventType(VideoEvent.EventType.PEDESTRIAN)
                        .location("서울외곽순환도로")
                        .cameraId("CAM-007")
                        .occurredAt(now.minusMinutes(52))
                        .status(VideoEvent.EventStatus.COMPLETED)
                        .severity(VideoEvent.Severity.NORMAL)
                        .description("보행자 감지 이벤트가 처리되었습니다.")
                        .processor("admin")
                        .processedAt(now.minusMinutes(45))
                        .build()
        );

        videoEventRepository.saveAll(events);
    }
}