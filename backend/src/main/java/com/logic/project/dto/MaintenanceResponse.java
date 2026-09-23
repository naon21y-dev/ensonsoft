package com.logic.project.dto;

import com.logic.project.domain.Maintenance;
import java.time.LocalDateTime;

public record MaintenanceResponse(
        Long id, String maintenanceCode,
        Long equipmentId, String equipmentCode, String equipmentName,
        Long siteId, String siteCode, String siteName,
        String title, String content, Maintenance.MaintenanceStatus status,
        LocalDateTime occurredAt, String reportedBy, String result,
        LocalDateTime reportedAt, LocalDateTime completedAt
) {
    public static MaintenanceResponse from(Maintenance maintenance) {
        var equipment = maintenance.getEquipment();
        var site = equipment.getSite();
        return new MaintenanceResponse(maintenance.getId(), maintenance.getMaintenanceCode(),
                equipment.getId(), equipment.getEquipmentCode(), equipment.getName(),
                site.getId(), site.getSiteCode(), site.getName(),
                maintenance.getTitle(), maintenance.getContent(), maintenance.getStatus(),
                maintenance.getOccurredAt(), maintenance.getReportedBy(), maintenance.getResult(),
                maintenance.getReportedAt(), maintenance.getCompletedAt());
    }
}
