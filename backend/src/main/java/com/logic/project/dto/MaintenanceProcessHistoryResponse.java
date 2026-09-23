package com.logic.project.dto;

import com.logic.project.domain.Maintenance;
import com.logic.project.domain.MaintenanceProcessHistory;
import java.time.LocalDateTime;

public record MaintenanceProcessHistoryResponse(
        Long id, Long maintenanceId,
        Maintenance.MaintenanceStatus previousStatus, Maintenance.MaintenanceStatus newStatus,
        String processedBy, String description, LocalDateTime processedAt
) {
    public static MaintenanceProcessHistoryResponse from(MaintenanceProcessHistory history) {
        return new MaintenanceProcessHistoryResponse(history.getId(), history.getMaintenance().getId(),
                history.getPreviousStatus(), history.getNewStatus(), history.getProcessedBy(),
                history.getDescription(), history.getProcessedAt());
    }
}
