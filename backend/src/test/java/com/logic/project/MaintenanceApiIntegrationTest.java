package com.logic.project;

import com.logic.project.config.JwtConfig;
import com.logic.project.domain.*;
import com.logic.project.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 임시 사용자/현장/장비/장애는 테스트 트랜잭션 종료 시 모두 롤백됩니다.
@SpringBootTest
@Transactional
class MaintenanceApiIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired JwtConfig jwtConfig;
    @Autowired MemberRepository memberRepository;
    @Autowired SiteRepository siteRepository;
    @Autowired EquipmentRepository equipmentRepository;
    @Autowired MaintenanceRepository maintenanceRepository;
    @Autowired MaintenanceProcessHistoryRepository historyRepository;
    @Autowired EntityManager entityManager;
    MockMvc mvc;
    Long equipmentId;
    Long otherEquipmentId;
    String reporter;
    String processor;
    String processorToken;

    @BeforeEach
    void setup() {
        String suffix = UUID.randomUUID().toString().substring(0, 12);
        reporter = "report-" + suffix;
        processor = "process-" + suffix;
        for (String username : new String[]{reporter, processor}) {
            memberRepository.saveAndFlush(Member.builder().username(username).password("unused-test-password")
                    .name("유지보수 테스트").email(username + "@example.test").build());
        }
        processorToken = jwtConfig.generateToken(processor, "USER");
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity())
                .defaultRequest(get("/").header("Authorization", "Bearer " + jwtConfig.generateToken(reporter, "USER"))).build();
        Site site = siteRepository.saveAndFlush(Site.builder().siteCode("MT-" + suffix)
                .name("유지보수 검증 현장").address("테스트 주소").siteType(Site.SiteType.ROAD).build());
        equipmentId = equipmentRepository.saveAndFlush(Equipment.builder().site(site).equipmentCode("MT-EQ-" + suffix)
                .name("검증 CCTV").equipmentType(Equipment.EquipmentType.CCTV).build()).getId();
        otherEquipmentId = equipmentRepository.saveAndFlush(Equipment.builder().site(site).equipmentCode("MT-OTHER-" + suffix)
                .name("다른 CCTV").equipmentType(Equipment.EquipmentType.CCTV).build()).getId();
    }

    String payload() {
        return """
                {"equipmentId":%d,"title":"영상 수신 장애","content":"화면이 보이지 않습니다.",
                 "occurredAt":"2026-09-23T10:00:00","reportedBy":"forged-user"}
                """.formatted(equipmentId);
    }

    Long create() throws Exception {
        mvc.perform(post("/api/maintenance").contentType(MediaType.APPLICATION_JSON).content(payload()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("REPORTED"))
                .andExpect(jsonPath("$.reportedBy").value(reporter))
                .andExpect(jsonPath("$.siteName").value("유지보수 검증 현장"))
                .andExpect(jsonPath("$.equipmentName").value("검증 CCTV"))
                .andExpect(jsonPath("$.maintenanceCode").isNotEmpty())
                .andExpect(jsonPath("$.reportedAt").isNotEmpty());
        entityManager.flush();
        entityManager.clear();
        return maintenanceRepository.findByEquipmentIdOrderByReportedAtDescIdDesc(equipmentId).getFirst().getId();
    }

    @Test
    void lifecyclePersistsActorResultAndHistoryWithoutChangingEquipment() throws Exception {
        Long id = create();
        mvc.perform(get("/api/maintenance")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
        mvc.perform(get("/api/maintenance/{id}", id)).andExpect(status().isOk());
        mvc.perform(get("/api/maintenance/equipment/{id}", equipmentId)).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/maintenance/equipment/{id}", otherEquipmentId)).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(patch("/api/maintenance/{id}/processing", id).header("Authorization", "Bearer " + processorToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        mvc.perform(patch("/api/maintenance/{id}/complete", id).header("Authorization", "Bearer " + processorToken)
                .contentType(MediaType.APPLICATION_JSON).content("{\"result\":\"케이블 교체 후 정상 수신 확인\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.completedAt").isNotEmpty());
        entityManager.flush();
        entityManager.clear();
        mvc.perform(get("/api/maintenance/{id}", id))
                .andExpect(jsonPath("$.result").value("케이블 교체 후 정상 수신 확인"))
                .andExpect(jsonPath("$.reportedBy").value(reporter));
        mvc.perform(get("/api/maintenance/{id}/histories", id))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].previousStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[0].newStatus").value("COMPLETED"))
                .andExpect(jsonPath("$[0].description").value("케이블 교체 후 정상 수신 확인"))
                .andExpect(jsonPath("$[0].processedBy").value(processor))
                .andExpect(jsonPath("$[0].processedAt").isNotEmpty())
                .andExpect(jsonPath("$[1].newStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[1].processedBy").value(processor))
                .andExpect(jsonPath("$[2].newStatus").value("REPORTED"))
                .andExpect(jsonPath("$[2].processedBy").value(reporter));
        assertEquals(Equipment.EquipmentStatus.NORMAL, equipmentRepository.findById(equipmentId).orElseThrow().getStatus());
    }

    @Test
    void invalidTransitionsNeverAppendHistoryOrOverwriteResult() throws Exception {
        Long id = create();
        mvc.perform(patch("/api/maintenance/{id}/complete", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"result\":\"조기 완료\"}")).andExpect(status().isBadRequest());
        assertEquals(1, historyRepository.findByMaintenanceIdOrderByProcessedAtDescIdDesc(id).size());
        mvc.perform(patch("/api/maintenance/{id}/processing", id)).andExpect(status().isOk());
        mvc.perform(patch("/api/maintenance/{id}/processing", id)).andExpect(status().isBadRequest());
        assertEquals(2, historyRepository.findByMaintenanceIdOrderByProcessedAtDescIdDesc(id).size());
        mvc.perform(patch("/api/maintenance/{id}/complete", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"result\":\"   \"}")).andExpect(status().isBadRequest());
        mvc.perform(patch("/api/maintenance/{id}/complete", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"result\":\"정상 복구\"}")).andExpect(status().isOk());
        mvc.perform(patch("/api/maintenance/{id}/complete", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"result\":\"덮어쓰기 시도\"}")).andExpect(status().isBadRequest());
        mvc.perform(patch("/api/maintenance/{id}/processing", id)).andExpect(status().isBadRequest());
        entityManager.flush();
        entityManager.clear();
        assertEquals(3, historyRepository.findByMaintenanceIdOrderByProcessedAtDescIdDesc(id).size());
        assertEquals("정상 복구", maintenanceRepository.findById(id).orElseThrow().getResult());
    }

    @Test
    void validatesInputAndMissingEquipment() throws Exception {
        mvc.perform(post("/api/maintenance").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/maintenance").contentType(MediaType.APPLICATION_JSON)
                .content(payload().replace("영상 수신 장애", " "))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/maintenance").contentType(MediaType.APPLICATION_JSON)
                .content(payload().replace("영상 수신 장애", "a".repeat(101)))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/maintenance").contentType(MediaType.APPLICATION_JSON)
                .content(payload().replace("2026-09-23T10:00:00", "invalid"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/maintenance").contentType(MediaType.APPLICATION_JSON)
                .content(payload().replace("\"equipmentId\":" + equipmentId, "\"equipmentId\":9223372036854775807")))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/maintenance/{id}", Long.MAX_VALUE)).andExpect(status().isBadRequest());
        mvc.perform(get("/api/maintenance/{id}/histories", Long.MAX_VALUE)).andExpect(status().isBadRequest());
        assertTrue(maintenanceRepository.findByEquipmentIdOrderByReportedAtDescIdDesc(equipmentId).isEmpty());
    }

    @Test
    void automaticCodesAreUniqueAcrossReports() throws Exception {
        Long first = create();
        Long second = create();
        assertNotEquals(first, second);
        var reports = maintenanceRepository.findByEquipmentIdOrderByReportedAtDescIdDesc(equipmentId);
        assertEquals(2, reports.size());
        assertNotEquals(reports.get(0).getMaintenanceCode(), reports.get(1).getMaintenanceCode());
    }

    @Test
    void anonymousCannotReadOrMutateMaintenance() throws Exception {
        MockMvc anonymous = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        var requests = java.util.List.of(get("/api/maintenance"), get("/api/maintenance/1/histories"),
                post("/api/maintenance").contentType(MediaType.APPLICATION_JSON).content(payload()),
                patch("/api/maintenance/1/processing"),
                patch("/api/maintenance/1/complete").contentType(MediaType.APPLICATION_JSON).content("{\"result\":\"test\"}"));
        for (var request : requests) {
            int status = anonymous.perform(request).andReturn().getResponse().getStatus();
            assertTrue(status == 302 || status == 401 || status == 403);
        }
    }
}
