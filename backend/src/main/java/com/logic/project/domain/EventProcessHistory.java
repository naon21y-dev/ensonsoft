package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_process_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventProcessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 영상 이벤트의 처리 이력인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_event_id", nullable = false)
    private VideoEvent videoEvent;

    // 처리 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VideoEvent.EventStatus status;

    // 처리 담당자
    @Column(nullable = false, length = 50)
    private String processor;

    // 처리 내용
    @Column(length = 500)
    private String description;

    // 처리 시간
    @Column(nullable = false, updatable = false)
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }
}