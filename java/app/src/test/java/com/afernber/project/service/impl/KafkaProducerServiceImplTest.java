package com.afernber.project.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceImplTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerServiceImpl service;

    @Test
    void sendEvent_ShouldSendToKafka() {
        when(kafkaTemplate.send(any(Message.class))).thenReturn(CompletableFuture.completedFuture(null));

        service.sendEvent("topic", "payload", "type", 1L);

        verify(kafkaTemplate).send(any(Message.class));
    }
}
