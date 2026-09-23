package com.logic.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MaintenanceCompleteRequest(
        @NotBlank(message = "처리 결과는 필수입니다.")
        @Size(max = 2000, message = "처리 결과는 2000자 이하로 입력해주세요.") String result
) {}
