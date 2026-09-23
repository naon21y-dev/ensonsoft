package com.logic.project.repository;

import com.logic.project.domain.VideoEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoEventRepository
        extends JpaRepository<VideoEvent, Long> {

    // 최신 이벤트 순으로 전체 조회
    List<VideoEvent> findAllByOrderByOccurredAtDesc();

    // 이벤트 처리 상태별 조회
    List<VideoEvent>
    findByStatusOrderByOccurredAtDesc(
            VideoEvent.EventStatus status
    );

    // 이벤트 종류별 조회
    List<VideoEvent>
    findByEventTypeOrderByOccurredAtDesc(
            VideoEvent.EventType eventType
    );

    // 위험도별 조회
    List<VideoEvent>
    findBySeverityOrderByOccurredAtDesc(
            VideoEvent.Severity severity
    );

    // 위치 검색
    List<VideoEvent>
    findByLocationContainingIgnoreCaseOrderByOccurredAtDesc(
            String location
    );
}