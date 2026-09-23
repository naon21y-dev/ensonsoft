package com.logic.project.dto;

import com.logic.project.domain.Equipment;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record EquipmentUpdateRequest(
    @NotNull(message = "소속 현장은 필수입니다.") @Positive
    Long siteId,
    @NotBlank(message = "장비명은 필수입니다.") @Size(max = 100)
    String name,
    @NotNull(message = "장비 유형은 필수입니다.")
    Equipment.EquipmentType equipmentType,
    @Size(max = 100)
    String manufacturer,
    @Size(max = 100)
    String modelName,
    @Size(max = 100)
    String serialNumber,
    @Size(max = 200)
    String installLocation,
    
    LocalDateTime installedAt,
    @Size(max = 45)
    String ipAddress,
    @Size(max = 500)
    String description
) {}
