package com.logic.project.repository;

import com.logic.project.domain.Equipment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {
    boolean existsByEquipmentCode(String equipmentCode);
    Optional<Equipment> findByEquipmentCode(String equipmentCode);
    boolean existsBySiteId(Long siteId);

    // Serialize edits/status changes so the previous status in the audit trail stays accurate.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Equipment e where e.id = :id")
    Optional<Equipment> findForUpdate(@Param("id") Long id);
}
