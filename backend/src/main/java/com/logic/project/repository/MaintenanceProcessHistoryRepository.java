package com.logic.project.repository;

import com.logic.project.domain.MaintenanceProcessHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceProcessHistoryRepository extends JpaRepository<MaintenanceProcessHistory, Long> {
    List<MaintenanceProcessHistory> findByMaintenanceIdOrderByProcessedAtDescIdDesc(Long maintenanceId);
}
