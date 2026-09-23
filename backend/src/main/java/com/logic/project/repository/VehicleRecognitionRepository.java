package com.logic.project.repository;

import com.logic.project.domain.VehicleRecognition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRecognitionRepository
        extends JpaRepository<VehicleRecognition, Long> {

    // 최신 차량번호 인식 데이터 순으로 전체 조회
    List<VehicleRecognition> findAllByOrderByRecognizedAtDesc();

    // 차량번호 검색
    List<VehicleRecognition>
    findByPlateNumberContainingIgnoreCaseOrderByRecognizedAtDesc(
            String plateNumber
    );

    // 위치 검색
    List<VehicleRecognition>
    findByLocationContainingIgnoreCaseOrderByRecognizedAtDesc(
            String location
    );

    // 상태별 조회
    List<VehicleRecognition>
    findByStatusOrderByRecognizedAtDesc(
            VehicleRecognition.RecognitionStatus status
    );
}