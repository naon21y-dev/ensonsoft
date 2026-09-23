package com.logic.project.repository;

import com.logic.project.domain.EventProcessHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventProcessHistoryRepository
        extends JpaRepository<EventProcessHistory, Long> {

    // 특정 이벤트의 처리 이력을 최신순으로 조회
    List<EventProcessHistory>
    findByVideoEventIdOrderByProcessedAtDesc(Long videoEventId);
}