package com.afernber.project.service.impl;

import com.afernber.project.constant.KafkaConstants;
import com.afernber.project.service.KafkaProducerService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendEvent(String topic, String payload, String eventType, Long originalId) {
        MessageBuilder<String> builder = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaConstants.EVENT_TYPE, eventType);

        if (originalId != null) {
            builder.setHeader(KafkaConstants.FAILED_EVENT_ID, originalId);
        }

        kafkaTemplate.send(builder.build());
    }
}