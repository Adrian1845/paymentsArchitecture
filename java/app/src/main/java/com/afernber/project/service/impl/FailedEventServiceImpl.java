package com.afernber.project.service.impl;

import com.afernber.project.domain.entity.FailedEventEntity;
import com.afernber.project.repository.FailedEventRepository;
import com.afernber.project.service.FailedEventService;
import com.afernber.project.service.KafkaProducerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FailedEventServiceImpl implements FailedEventService {

    private final FailedEventRepository repository;
    private final KafkaProducerService producerService;

    @Override
    public void saveOrUpdateFailedEvent(String payload, String topic, String error, String type, Long existingId) {
        String mainTopic = topic;
        if (topic != null && topic.endsWith("-dlt")) {
            mainTopic = topic.replace("-dlt", "");
        }

        FailedEventEntity entity;

        if (existingId != null && repository.existsById(existingId)) {
            entity = repository.findById(existingId).get();
            entity.setErrorMessage(error);
            entity.setRetryCount(entity.getRetryCount() + 1);
            entity.setOccurredAt(LocalDateTime.now());
            entity.setStatus(entity.getRetryCount() >= 5 ? "CRITICAL_FAILURE" : "PENDING_REPLAY");
        } else {
            entity = FailedEventEntity.builder()
                    .payload(payload)
                    .sourceTopic(mainTopic)
                    .errorMessage(error)
                    .eventType(type)
                    .occurredAt(LocalDateTime.now())
                    .retryCount(0)
                    .status("PENDING_REPLAY")
                    .build();
        }

        repository.save(entity);
    }

    @Transactional
    @Override
    public void replayEvent(Long id) {
        FailedEventEntity event = repository.findById(id).orElseThrow();

        producerService.sendEvent(event.getSourceTopic(), event.getPayload(), event.getEventType(), event.getId());

        event.setStatus("REPLAYED");
        repository.save(event);
    }

    @Transactional
    @Override
    public void replayAllEvents() {
        List<FailedEventEntity> pending = repository.findByStatus("PENDING_REPLAY");
        log.info("Starting replay for {} events...", pending.size());
        pending.forEach(event -> replayEvent(event.getId()));
    }
}
