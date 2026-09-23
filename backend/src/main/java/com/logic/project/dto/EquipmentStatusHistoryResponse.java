package com.logic.project.dto;

import com.logic.project.domain.Equipment;
import com.logic.project.domain.EquipmentStatusHistory;

import java.time.LocalDateTime;

public record EquipmentStatusHistoryResponse(

        Long id,

        Long equipmentId,

        String equipmentCode,

        String equipmentName,

        Equipment.EquipmentStatus previousStatus,

        Equipment.EquipmentStatus newStatus,

        String changedBy,

        String reason,

        LocalDateTime changedAt

) {

    public static EquipmentStatusHistoryResponse from(
            EquipmentStatusHistory history
    ) {

        return new EquipmentStatusHistoryResponse(
                history.getId(),
                history.getEquipment().getId(),
                history.getEquipment().getEquipmentCode(),
                history.getEquipment().getName(),
                history.getPreviousStatus(),
                history.getNewStatus(),
                history.getChangedBy(),
                history.getReason(),
                history.getChangedAt()
        );
    }
}