package com.afernber.project.service.impl;

import com.afernber.project.service.KafkaProducerService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendEvent(String topic, String payload, String eventType) {
        Message<String> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("event_type", eventType)
                .build();

        kafkaTemplate.send(message);
    }
}