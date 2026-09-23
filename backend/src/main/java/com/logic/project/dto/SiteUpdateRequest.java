package com.logic.project.dto;

import com.logic.project.domain.Site;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SiteUpdateRequest(

        @NotBlank(message = "현장명은 필수입니다.")
        @Size(max = 100, message = "현장명은 100자 이하로 입력해주세요.")
        String name,

        @NotBlank(message = "주소는 필수입니다.")
        @Size(max = 200, message = "주소는 200자 이하로 입력해주세요.")
        String address,

        @Size(max = 500, message = "설명은 500자 이하로 입력해주세요.")
        String description,

        @NotNull(message = "현장 유형은 필수입니다.")
        Site.SiteType siteType,

        @Size(max = 50, message = "담당자명은 50자 이하로 입력해주세요.")
        String managerName,

        @Size(max = 30, message = "연락처는 30자 이하로 입력해주세요.")
        String managerTel

) {
}