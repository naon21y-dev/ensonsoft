package com.logic.project;

import com.logic.project.config.JwtConfig;
import com.logic.project.domain.Member;
import com.logic.project.domain.Site;
import com.logic.project.repository.*;
import com.logic.project.service.SiteService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

// Real PostgreSQL/JPA with transaction rollback; no existing records are modified.
@SpringBootTest
@Transactional
class EquipmentApiIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired JwtConfig jwtConfig;
    @Autowired MemberRepository memberRepository;
    @Autowired SiteRepository siteRepository;
    @Autowired EquipmentRepository equipmentRepository;
    @Autowired EquipmentStatusHistoryRepository historyRepository;
    @Autowired SiteService siteService;
    MockMvc mvc;
    Long siteId;
    String code;
    String username;

    @BeforeEach
    void setup() {
        String suffix = UUID.randomUUID().toString().substring(0, 12);
        username = "test-" + suffix;
        memberRepository.saveAndFlush(Member.builder().username(username).password("unused-test-password")
                .name("테스트 사용자").email(username + "@example.test").build());
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity())
                .defaultRequest(get("/").header("Authorization", "Bearer " + jwtConfig.generateToken(username, "USER"))).build();
        siteId = siteRepository.saveAndFlush(Site.builder().siteCode("TEST-" + suffix)
                .name("검증 현장").address("테스트 주소").siteType(Site.SiteType.ROAD).build()).getId();
        code = "TEST-EQ-" + suffix;
    }

    String payload() {
        return """
                {"siteId":%d,"equipmentCode":"%s","name":"검증 CCTV", "equipmentType":"CCTV",
                 "installedAt":"2026-09-23T10:00:00","ipAddress":"192.168.0.101"}
                """.formatted(siteId, code);
    }

    @Test
    void completeLifecycleAndCombinedFilters() throws Exception {
        mvc.perform(post("/api/equipments").contentType(MediaType.APPLICATION_JSON).content(payload()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("NORMAL"))
                .andExpect(jsonPath("$.siteName").value("검증 현장"));
        Long id = equipmentRepository.findByEquipmentCode(code).orElseThrow().getId();
        assertThrows(IllegalArgumentException.class, () -> siteService.deleteSite(siteId));
        mvc.perform(get("/api/equipments/code/{code}", code)).andExpect(status().isOk());
        mvc.perform(get("/api/equipments/site/{id}", siteId)).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/equipments").param("siteId", siteId.toString()).param("name", "CCTV")
                .param("status", "NORMAL").param("equipmentType", "CCTV"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/equipments").param("siteId", siteId.toString()).param("status", "ERROR"))
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(patch("/api/equipments/{id}/status", id).param("status", "ERROR")
                .param("reason", "통신장애"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ERROR"));
        mvc.perform(get("/api/equipments/{id}/histories", id))
                .andExpect(jsonPath("$[0].previousStatus").value("NORMAL"))
                .andExpect(jsonPath("$[0].newStatus").value("ERROR"))
                .andExpect(jsonPath("$[0].changedBy").value(username))
                .andExpect(jsonPath("$[0].reason").value("통신장애"))
                .andExpect(jsonPath("$[0].changedAt").isNotEmpty());
        mvc.perform(put("/api/equipments/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(payload().replace("검증 CCTV", "수정 CCTV")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("수정 CCTV"))
                .andExpect(jsonPath("$.status").value("ERROR"));
        mvc.perform(delete("/api/equipments/{id}", id)).andExpect(status().isNoContent());
        equipmentRepository.flush();
        assertFalse(equipmentRepository.existsById(id));
        assertTrue(historyRepository.findByEquipmentIdOrderByChangedAtDesc(id).isEmpty());
        assertTrue(siteRepository.existsById(siteId));
    }

    @Test
    void invalidRequestsReturnBadRequest() throws Exception {
        mvc.perform(post("/api/equipments").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/equipments/status/INVALID")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/equipments").contentType(MediaType.APPLICATION_JSON)
                .content(payload().replace("2026-09-23T10:00:00", "invalid-date")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/equipments").contentType(MediaType.APPLICATION_JSON).content(payload()))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/equipments").contentType(MediaType.APPLICATION_JSON).content(payload()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void anonymousRequestsCannotReadEquipment() throws Exception {
        MockMvc anonymous = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        int status = anonymous.perform(get("/api/equipments")).andReturn().getResponse().getStatus();
        assertTrue(status == 302 || status == 401 || status == 403);
    }
}
