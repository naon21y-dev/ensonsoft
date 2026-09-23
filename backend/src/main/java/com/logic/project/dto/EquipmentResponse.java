package com.logic.project.dto;
import com.logic.project.domain.Equipment;
import java.time.LocalDateTime;

public record EquipmentResponse(
    Long id,
    Long siteId,
    String siteCode,
    String siteName,
    String equipmentCode,
    String name,
    Equipment.EquipmentType equipmentType,
    String manufacturer,
    String modelName,
    String serialNumber,
    String installLocation,
    LocalDateTime installedAt,
    String ipAddress,
    String description,
    Equipment.EquipmentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static EquipmentResponse from(Equipment e) {
        return new EquipmentResponse(
            e.getId(),
            e.getSite().getId(),
            e.getSite().getSiteCode(),
            e.getSite().getName(),
            e.getEquipmentCode(),
            e.getName(),
            e.getEquipmentType(),
            e.getManufacturer(),
            e.getModelName(),
            e.getSerialNumber(),
            e.getInstallLocation(),
            e.getInstalledAt(),
            e.getIpAddress(),
            e.getDescription(),
            e.getStatus(),
            e.getCreatedAt(),
            e.getUpdatedAt()
        );
    }
}
