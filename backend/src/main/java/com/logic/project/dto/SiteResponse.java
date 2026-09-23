package com.logic.project.dto;

import com.logic.project.domain.Site;

import java.time.LocalDateTime;

public record SiteResponse(

        Long id,

        String siteCode,

        String name,

        String address,

        String description,

        Site.SiteType siteType,

        Site.SiteStatus status,

        String managerName,

        String managerTel,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {

    public static SiteResponse from(
            Site site
    ) {

        return new SiteResponse(
                site.getId(),
                site.getSiteCode(),
                site.getName(),
                site.getAddress(),
                site.getDescription(),
                site.getSiteType(),
                site.getStatus(),
                site.getManagerName(),
                site.getManagerTel(),
                site.getCreatedAt(),
                site.getUpdatedAt()
        );
    }
}