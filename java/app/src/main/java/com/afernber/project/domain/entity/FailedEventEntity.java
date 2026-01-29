package com.afernber.project.domain.entity;

import com.afernber.project.constant.EventTypeConstants;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "failed_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailedEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "source_topic", nullable = false)
    private String sourceTopic;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Builder.Default
    private String status = EventTypeConstants.PENDING_REPLAY;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "retry_count")
    private Integer retryCount;

    @PrePersist
    protected void onCreate() {
        occurredAt = LocalDateTime.now();
    }
}