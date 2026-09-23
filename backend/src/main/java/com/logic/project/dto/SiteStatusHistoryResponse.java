package com.logic.project.dto;

import com.logic.project.domain.Site;
import com.logic.project.domain.SiteStatusHistory;

import java.time.LocalDateTime;

public record SiteStatusHistoryResponse(

        Long id,

        Long siteId,

        String siteCode,

        String siteName,

        Site.SiteStatus previousStatus,

        Site.SiteStatus newStatus,

        String changedBy,

        String reason,

        LocalDateTime changedAt

) {

    public static SiteStatusHistoryResponse from(
            SiteStatusHistory history
    ) {

        return new SiteStatusHistoryResponse(
                history.getId(),
                history.getSite().getId(),
                history.getSite().getSiteCode(),
                history.getSite().getName(),
                history.getPreviousStatus(),
                history.getNewStatus(),
                history.getChangedBy(),
                history.getReason(),
                history.getChangedAt()
        );
    }
}