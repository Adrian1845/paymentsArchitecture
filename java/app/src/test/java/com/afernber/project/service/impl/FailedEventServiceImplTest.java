package com.afernber.project.service.impl;

import com.afernber.project.constant.EventTypeConstants;
import com.afernber.project.domain.entity.FailedEventEntity;
import com.afernber.project.repository.FailedEventRepository;
import com.afernber.project.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FailedEventServiceImplTest {

    @Mock private FailedEventRepository repository;
    @Mock private KafkaProducerService producerService;

    @InjectMocks private FailedEventServiceImpl service;

    @Test
    void saveFailedEvent_ShouldSave() {
        service.saveOrUpdateFailedEvent("topic", "payload", "error", "type", 1L);
        verify(repository).save(any());
    }

    @Test
    void replayEvent_ShouldResendAndDelete() {
        FailedEventEntity event = new FailedEventEntity();
        event.setId(1L);
        event.setSourceTopic("topic");
        event.setPayload("payload");
        event.setEventType("type");

        when(repository.findById(1L)).thenReturn(Optional.of(event));

        service.replayEvent(1L);

        verify(producerService).sendEvent(eq("topic"), eq("payload"), eq("type"), any());
    }

    @Test
    void replayAllEvents_ShouldReplayAll() {
        FailedEventEntity event = new FailedEventEntity();
        event.setId(1L);
        event.setSourceTopic("topic");
        event.setPayload("payload");
        event.setEventType("type");

        when(repository.findByStatus(EventTypeConstants.PENDING_REPLAY)).thenReturn(List.of(event));
        when(repository.findById(1L)).thenReturn(Optional.of(event));

        service.replayAllEvents();

        verify(producerService).sendEvent(any(), any(), any(), any());
    }
}