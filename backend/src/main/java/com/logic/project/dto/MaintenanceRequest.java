package com.logic.project.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record MaintenanceRequest(
        @NotNull(message = "장비는 필수입니다.") @Positive Long equipmentId,
        @NotBlank(message = "제목은 필수입니다.") @Size(max = 100, message = "제목은 100자 이하로 입력해주세요.") String title,
        @NotBlank(message = "장애 내용은 필수입니다.") @Size(max = 2000, message = "장애 내용은 2000자 이하로 입력해주세요.") String content,
        @NotNull(message = "발생 시간은 필수입니다.") LocalDateTime occurredAt
) {}
