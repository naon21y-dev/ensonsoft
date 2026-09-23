package com.logic.project;

import com.logic.project.domain.Site;
import com.logic.project.service.DemoSeedService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DemoSeedIntegrationTest {
    @Autowired DemoSeedService seeder;
    @Autowired EntityManager em;

    Map<String, Long> counts() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (String entity : new String[]{"Site", "Equipment", "Maintenance", "VideoEvent", "SiteStatusHistory", "EquipmentStatusHistory", "MaintenanceProcessHistory", "EventProcessHistory"}) {
            result.put(entity, em.createQuery("select count(e) from " + entity + " e", Long.class).getSingleResult());
        }
        return result;
    }

    @Test
    void addsCompleteDemoSetWithoutDuplicatingOrOverwritingExistingRows() {
        Site existing = Site.builder().siteCode("KEEP-" + UUID.randomUUID().toString().substring(0, 12))
                .name("기존 현장 보존").address("기존 주소").siteType(Site.SiteType.ROAD).status(Site.SiteStatus.WARNING).build();
        em.persist(existing);
        em.flush();
        var before = counts();
        seeder.seed();
        var first = counts();
        seeder.seed();
        assertEquals(first, counts());
        before.forEach((key, count) -> assertTrue(first.get(key) >= count));
        em.clear();
        assertEquals("기존 현장 보존", em.find(Site.class, existing.getId()).getName());
        assertEquals(Site.SiteStatus.WARNING, em.find(Site.class, existing.getId()).getStatus());
        assertEquals(10L, em.createQuery("select count(s) from Site s where s.siteCode like 'DEMO-SITE-%'", Long.class).getSingleResult());
        assertEquals(40L, em.createQuery("select count(e) from Equipment e where e.equipmentCode like 'DEMO-EQ-%'", Long.class).getSingleResult());
        assertEquals(18L, em.createQuery("select count(m) from Maintenance m where m.maintenanceCode like 'DEMO-MNT-%'", Long.class).getSingleResult());
        assertEquals(24L, em.createQuery("select count(e) from VideoEvent e where e.cameraId like 'DEMO-EVENT-%'", Long.class).getSingleResult());
        assertEquals(36L, em.createQuery("select count(h) from MaintenanceProcessHistory h where h.maintenance.maintenanceCode like 'DEMO-MNT-%'", Long.class).getSingleResult());
    }
}
