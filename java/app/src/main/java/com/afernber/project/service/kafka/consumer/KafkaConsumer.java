package com.afernber.project.service.kafka.consumer;

import com.afernber.project.service.kafka.factory.KafkaStrategyFactory;
import com.afernber.project.service.kafka.strategies.KafkaStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaConsumer {

    private final KafkaStrategyFactory strategyFactory;

    @KafkaListener(topics = "payments-events-topic")
    public void listen(String message, @Header("event_type") String eventType) {
        log.info("Kafka Message Received | Type: {} | Payload: {}", eventType, message);

        try {
            KafkaStrategy strategy = strategyFactory.getStrategy(eventType);
            strategy.execute(message, eventType);
        } catch (Exception e) {
            log.error("Error processing Kafka event: {}", e.getMessage());
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("💀 DEAD LETTER ALERT: Message from topic {} moved to DLT. Content: {}", topic, message);
        // TODO: handle event errors
    }
}
