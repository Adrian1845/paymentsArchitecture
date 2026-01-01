package com.afernber.project.service.kafka.consumer;

import com.afernber.project.constant.KafkaConstants;
import com.afernber.project.service.FailedEventService;
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
    private final FailedEventService failedEventService;
    @KafkaListener(topics = KafkaConstants.PAYMENTS_TOPIC)
    public void listen(String message, @Header("event_type") String eventType) {
        log.info("Kafka Message Received | Type: {} | Payload: {}", eventType, message);
        try {
            KafkaStrategy strategy = strategyFactory.getStrategy(eventType);
            strategy.execute(message, eventType);
        } catch (Exception e) {
            log.error("Error processing Kafka event: {}", e.getMessage());
            throw e;
        }
    }

    @DltHandler
    public void handleDlt(String message,
                          @Header(value = KafkaHeaders.RECEIVED_TOPIC, required = false) String topic,
                          @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String exceptionMessage,
                          @Header(KafkaConstants.EVENT_TYPE) String eventType,
                          @Header(value = KafkaConstants.FAILED_EVENT_ID, required = false) Long existingId) {

        log.error("💀 Moving failed event to DB from topic: {}", topic);

        failedEventService.saveOrUpdateFailedEvent(message, topic, exceptionMessage, eventType, existingId);
    }
}
