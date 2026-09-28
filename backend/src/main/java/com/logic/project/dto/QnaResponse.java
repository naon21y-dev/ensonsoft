package com.logic.project.dto;

import com.logic.project.domain.Qna;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QnaResponse {

    private Long id;

    private Qna.Category category;

    private String title;

    private String content;

    private String username;

    private boolean secret;

    private Qna.Status status;

    private String answer;

    private String answeredBy;

    private LocalDateTime answeredAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static QnaResponse from(Qna qna) {

        return QnaResponse.builder()
                .id(qna.getId())
                .category(qna.getCategory())
                .title(qna.getTitle())
                .content(qna.getContent())
                .username(qna.getUsername())
                .secret(qna.isSecret())
                .status(qna.getStatus())
                .answer(qna.getAnswer())
                .answeredBy(qna.getAnsweredBy())
                .answeredAt(qna.getAnsweredAt())
                .createdAt(qna.getCreatedAt())
                .updatedAt(qna.getUpdatedAt())
                .build();
    }
}
