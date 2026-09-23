package com.logic.project;

import com.logic.project.config.JwtConfig;
import com.logic.project.domain.*;
import com.logic.project.dto.DashboardResponse;
import com.logic.project.repository.*;
import com.logic.project.service.DashboardService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 기존 데이터에 테스트 데이터를 더해 증가분을 검증하고 종료 시 롤백합니다.
@SpringBootTest
@Transactional
class DashboardApiIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired JwtConfig jwtConfig;
    @Autowired MemberRepository memberRepository;
    @Autowired SiteRepository siteRepository;
    @Autowired EquipmentRepository equipmentRepository;
    @Autowired MaintenanceRepository maintenanceRepository;
    @Autowired VideoEventRepository eventRepository;
    @Autowired DashboardService dashboardService;
    @Autowired EntityManager entityManager;
    MockMvc mvc;
    String suffix;

    @BeforeEach
    void setup() {
        suffix = UUID.randomUUID().toString().substring(0, 12);
        String username = "dash-" + suffix;
        memberRepository.saveAndFlush(Member.builder().username(username).password("unused-test-password")
                .name("대시보드 테스트").email(username + "@example.test").build());
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity())
                .defaultRequest(get("/").header("Authorization", "Bearer " + jwtConfig.generateToken(username, "USER"))).build();
    }

    void addFixture() {
        Site firstSite = null;
        for (var status : Site.SiteStatus.values()) {
            Site site = siteRepository.saveAndFlush(Site.builder().siteCode("D-" + status.ordinal() + "-" + suffix)
                    .name("대시보드 검증 현장").address("검증 주소").siteType(Site.SiteType.ROAD).status(status).build());
            if (firstSite == null) firstSite = site;
        }
        Equipment firstEquipment = null;
        for (var status : Equipment.EquipmentStatus.values()) {
            Equipment equipment = equipmentRepository.saveAndFlush(Equipment.builder().site(firstSite)
                    .equipmentCode("D-E" + status.ordinal() + "-" + suffix).name("검증 장비")
                    .equipmentType(Equipment.EquipmentType.CCTV).status(status).build());
            if (firstEquipment == null) firstEquipment = equipment;
        }
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 7; i++) {
            // 같은 발생 시간도 포함하여 ID 내림차순 보조 정렬을 검증합니다.
            LocalDateTime occurredAt = now.minusMinutes(i / 2);
            maintenanceRepository.saveAndFlush(Maintenance.builder().equipment(firstEquipment)
                    .maintenanceCode("D-M" + i + "-" + suffix).title("검증 장애 " + i).content("내용")
                    .occurredAt(occurredAt).reportedAt(now.plusMinutes(i)).reportedBy("dash-" + suffix)
                    .status(Maintenance.MaintenanceStatus.values()[i % 3]).build());
            eventRepository.saveAndFlush(VideoEvent.builder().eventType(VideoEvent.EventType.ACCIDENT)
                    .location("검증 위치").cameraId("D-CAM").occurredAt(occurredAt)
                    .status(VideoEvent.EventStatus.values()[i % 3]).build());
        }
        entityManager.clear();
    }

    @Test
    void aggregatesActualDatabaseAndAuthenticatesApi() throws Exception {
        DashboardResponse before = dashboardService.getDashboard();
        addFixture();
        DashboardResponse after = dashboardService.getDashboard();
        assertEquals(before.totalSites() + 5, after.totalSites());
        assertEquals(before.totalEquipments() + 6, after.totalEquipments());
        assertEquals(before.normalEquipments() + 1, after.normalEquipments());
        assertEquals(before.errorEquipments() + 1, after.errorEquipments());
        assertEquals(before.pendingMaintenances() + 5, after.pendingMaintenances());
        assertEquals(before.totalEvents() + 7, after.totalEvents());
        for (var status : Site.SiteStatus.values()) {
            assertEquals(before.siteStatuses().get(status.name()) + 1, after.siteStatuses().get(status.name()));
        }
        for (var status : Equipment.EquipmentStatus.values()) {
            assertEquals(before.equipmentStatuses().get(status.name()) + 1, after.equipmentStatuses().get(status.name()));
        }
        for (var status : Maintenance.MaintenanceStatus.values()) {
            long delta = status == Maintenance.MaintenanceStatus.REPORTED ? 3 : 2;
            assertEquals(before.maintenanceStatuses().get(status.name()) + delta, after.maintenanceStatuses().get(status.name()));
        }
        for (var status : VideoEvent.EventStatus.values()) {
            long delta = status == VideoEvent.EventStatus.UNPROCESSED ? 3 : 2;
            assertEquals(before.eventStatuses().get(status.name()) + delta, after.eventStatuses().get(status.name()));
        }
        mvc.perform(get("/api/dashboard")).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSites").value(after.totalSites()))
                .andExpect(jsonPath("$.pendingMaintenances").value(after.pendingMaintenances()))
                .andExpect(jsonPath("$.equipmentStatuses.OFFLINE").value(after.equipmentStatuses().get("OFFLINE")))
                .andExpect(jsonPath("$.recentMaintenances.length()").value(5))
                .andExpect(jsonPath("$.recentEvents.length()").value(5))
                .andExpect(jsonPath("$.generatedAt").isNotEmpty());
    }

    @Test
    void recentListsAreLimitedAndSortedByOccurrenceThenIdWithSiteInformation() {
        addFixture();
        var dashboard = dashboardService.getDashboard();
        var expectedMaintenance = maintenanceRepository.findAll().stream()
                .sorted(Comparator.comparing(Maintenance::getOccurredAt).thenComparing(Maintenance::getId).reversed())
                .limit(5).toList();
        var expectedEvents = eventRepository.findAll().stream()
                .sorted(Comparator.comparing(VideoEvent::getOccurredAt).thenComparing(VideoEvent::getId).reversed())
                .limit(5).toList();
        assertEquals(expectedMaintenance.stream().map(Maintenance::getId).toList(),
                dashboard.recentMaintenances().stream().map(item -> item.id()).toList());
        assertEquals(expectedEvents.stream().map(VideoEvent::getId).toList(),
                dashboard.recentEvents().stream().map(item -> item.getId()).toList());
        for (int i = 0; i < expectedMaintenance.size(); i++) {
            assertEquals(expectedMaintenance.get(i).getEquipment().getSite().getName(), dashboard.recentMaintenances().get(i).siteName());
            assertEquals(expectedMaintenance.get(i).getEquipment().getName(), dashboard.recentMaintenances().get(i).equipmentName());
        }
    }

    @Test
    void anonymousCannotReadDashboard() throws Exception {
        MockMvc anonymous = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        int status = anonymous.perform(get("/api/dashboard")).andReturn().getResponse().getStatus();
        assertTrue(status == 302 || status == 401 || status == 403);
    }
}
