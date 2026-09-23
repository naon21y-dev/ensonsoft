package com.logic.project.repository;

import com.logic.project.domain.Maintenance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    @EntityGraph(attributePaths = {"equipment", "equipment.site"})
    List<Maintenance> findAllByOrderByReportedAtDescIdDesc();

    @EntityGraph(attributePaths = {"equipment", "equipment.site"})
    List<Maintenance> findByEquipmentIdOrderByReportedAtDescIdDesc(Long equipmentId);

    // 동시에 처리 요청이 들어와도 동일 단계의 이력이 중복되지 않도록 잠급니다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Maintenance m where m.id = :id")
    Optional<Maintenance> findForUpdate(@Param("id") Long id);
}
