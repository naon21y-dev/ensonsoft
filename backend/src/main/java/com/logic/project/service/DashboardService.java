package com.logic.project.service;

import com.logic.project.domain.*;
import com.logic.project.dto.*;
import com.logic.project.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository dashboardRepository;

    // 한 응답 내 집계와 최근 목록은 동일한 DB 스냅샷을 사용합니다.
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public DashboardResponse getDashboard() {
        var sites = withAllStatuses(Site.SiteStatus.values(), dashboardRepository.countSitesByStatus());
        var equipments = withAllStatuses(Equipment.EquipmentStatus.values(), dashboardRepository.countEquipmentsByStatus());
        var maintenances = withAllStatuses(Maintenance.MaintenanceStatus.values(), dashboardRepository.countMaintenancesByStatus());
        var events = withAllStatuses(VideoEvent.EventStatus.values(), dashboardRepository.countEventsByStatus());

        return new DashboardResponse(total(sites), total(equipments), equipments.get("NORMAL"), equipments.get("ERROR"),
                maintenances.get("REPORTED") + maintenances.get("IN_PROGRESS"), total(events),
                equipments, sites, maintenances, events,
                dashboardRepository.findRecentMaintenances().stream().map(MaintenanceResponse::from).toList(),
                dashboardRepository.findRecentEvents().stream().map(VideoEventResponse::from).toList(),
                LocalDateTime.now());
    }

    private Map<String, Long> withAllStatuses(Enum<?>[] statuses, Map<String, Long> counts) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Enum<?> status : statuses) result.put(status.name(), counts.getOrDefault(status.name(), 0L));
        return result;
    }

    private long total(Map<String, Long> counts) {
        return counts.values().stream().mapToLong(Long::longValue).sum();
    }
}
