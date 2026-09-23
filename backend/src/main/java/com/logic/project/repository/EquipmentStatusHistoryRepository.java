package com.logic.project.repository;

import com.logic.project.domain.EquipmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentStatusHistoryRepository
        extends JpaRepository<EquipmentStatusHistory, Long> {

    // 특정 장비의 상태 변경 이력
    // 최신 변경순
    List<EquipmentStatusHistory>
    findByEquipmentIdOrderByChangedAtDesc(Long equipmentId);
    void deleteByEquipmentId(Long equipmentId);
}