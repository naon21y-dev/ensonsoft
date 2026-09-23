package com.logic.project.dto;

import com.logic.project.domain.EventProcessHistory;
import com.logic.project.domain.VideoEvent;

import java.time.LocalDateTime;

public record EventProcessHistoryResponse(

        Long id,
        Long videoEventId,
        VideoEvent.EventStatus status,
        String processor,
        String description,
        LocalDateTime processedAt

) {

    public static EventProcessHistoryResponse from(
            EventProcessHistory history
    ) {
        return new EventProcessHistoryResponse(
                history.getId(),
                history.getVideoEvent().getId(),
                history.getStatus(),
                history.getProcessor(),
                history.getDescription(),
                history.getProcessedAt()
        );
    }
}