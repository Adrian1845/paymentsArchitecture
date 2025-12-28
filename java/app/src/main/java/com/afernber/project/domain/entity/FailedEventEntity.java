package com.afernber.project.domain.entity;

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

    @Column(nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(nullable = false)
    private String sourceTopic;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Builder.Default
    private String status = "PENDING_REPLAY";

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    private Integer retryCount;

    @PrePersist
    protected void onCreate() {
        occurredAt = LocalDateTime.now();
    }
}