package com.logic.project.repository;

import com.logic.project.domain.Maintenance;
import com.logic.project.domain.VideoEvent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 대시보드 전용 읽기 쿼리. 기존 관리 기능의 Repository는 변경하지 않습니다.
@Repository
@RequiredArgsConstructor
public class DashboardRepository {
    private final EntityManager entityManager;

    public Map<String, Long> countSitesByStatus() { return countByStatus("Site"); }
    public Map<String, Long> countEquipmentsByStatus() { return countByStatus("Equipment"); }
    public Map<String, Long> countMaintenancesByStatus() { return countByStatus("Maintenance"); }
    public Map<String, Long> countEventsByStatus() { return countByStatus("VideoEvent"); }

    private Map<String, Long> countByStatus(String entityName) {
        // entityName은 위 메서드의 고정된 Entity 이름만 사용합니다.
        List<Object[]> rows = entityManager.createQuery(
                "select e.status, count(e) from " + entityName + " e group by e.status", Object[].class)
                .getResultList();
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : rows) counts.put(((Enum<?>) row[0]).name(), ((Number) row[1]).longValue());
        return counts;
    }

    public List<Maintenance> findRecentMaintenances() {
        return entityManager.createQuery("""
                select m from Maintenance m
                join fetch m.equipment e join fetch e.site
                order by m.occurredAt desc, m.id desc
                """, Maintenance.class).setMaxResults(5).getResultList();
    }

    public List<VideoEvent> findRecentEvents() {
        return entityManager.createQuery(
                "select e from VideoEvent e order by e.occurredAt desc, e.id desc", VideoEvent.class)
                .setMaxResults(5).getResultList();
    }
}
