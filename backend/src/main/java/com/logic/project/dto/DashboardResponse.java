package com.logic.project.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalSites,
        long totalEquipments,
        long normalEquipments,
        long errorEquipments,
        long pendingMaintenances,
        long totalEvents,
        Map<String, Long> equipmentStatuses,
        Map<String, Long> siteStatuses,
        Map<String, Long> maintenanceStatuses,
        Map<String, Long> eventStatuses,
        List<MaintenanceResponse> recentMaintenances,
        List<VideoEventResponse> recentEvents,
        LocalDateTime generatedAt
) {}
