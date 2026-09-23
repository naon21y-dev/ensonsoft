package com.logic.project.repository;

import com.logic.project.domain.SiteStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteStatusHistoryRepository
        extends JpaRepository<SiteStatusHistory, Long> {

    // 특정 현장의 상태 변경 이력
    // 최신 변경순
    List<SiteStatusHistory>
    findBySiteIdOrderByChangedAtDesc(Long siteId);
}