package com.logic.project.service;

import com.logic.project.repository.DashboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
    @Mock DashboardRepository repository;
    DashboardService service;

    @BeforeEach
    void setup() {
        service = new DashboardService(repository);
        when(repository.findRecentMaintenances()).thenReturn(List.of());
        when(repository.findRecentEvents()).thenReturn(List.of());
    }

    @Test
    void emptyDatabaseReturnsAllStatusesWithZeroAndEmptyLists() {
        when(repository.countSitesByStatus()).thenReturn(Map.of());
        when(repository.countEquipmentsByStatus()).thenReturn(Map.of());
        when(repository.countMaintenancesByStatus()).thenReturn(Map.of());
        when(repository.countEventsByStatus()).thenReturn(Map.of());
        var response = service.getDashboard();
        assertEquals(0, response.totalSites());
        assertEquals(0, response.totalEquipments());
        assertEquals(0, response.pendingMaintenances());
        assertEquals(0, response.totalEvents());
        assertEquals(6, response.equipmentStatuses().size());
        assertEquals(5, response.siteStatuses().size());
        assertEquals(3, response.maintenanceStatuses().size());
        assertEquals(3, response.eventStatuses().size());
        assertTrue(response.equipmentStatuses().values().stream().allMatch(count -> count == 0L));
        assertTrue(response.recentMaintenances().isEmpty());
        assertTrue(response.recentEvents().isEmpty());
        assertNotNull(response.generatedAt());
    }

    @Test
    void pendingIncludesReportedAndInProgressButNotCompletedAndCountsRemainLong() {
        when(repository.countSitesByStatus()).thenReturn(Map.of("INACTIVE", 2L));
        when(repository.countEquipmentsByStatus()).thenReturn(Map.of("NORMAL", 3_000_000_000L, "ERROR", 3L, "OFFLINE", 4L));
        when(repository.countMaintenancesByStatus()).thenReturn(Map.of("REPORTED", 2L, "IN_PROGRESS", 3L, "COMPLETED", 7L));
        when(repository.countEventsByStatus()).thenReturn(Map.of("UNPROCESSED", 2L, "PROCESSING", 3L, "COMPLETED", 4L));
        var response = service.getDashboard();
        assertEquals(2, response.totalSites());
        assertEquals(3_000_000_007L, response.totalEquipments());
        assertEquals(3_000_000_000L, response.normalEquipments());
        assertEquals(3, response.errorEquipments());
        assertEquals(5, response.pendingMaintenances());
        assertEquals(9, response.totalEvents());
        assertEquals(0L, response.equipmentStatuses().get("WARNING"));
    }
}
