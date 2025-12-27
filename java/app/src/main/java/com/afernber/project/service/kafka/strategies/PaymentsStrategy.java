package com.afernber.project.service.kafka.strategies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PaymentsStrategy implements KafkaStrategy {
    @Override
    public void execute(String message, String eventType) {
        log.info("💰 Strategy: Processing Payment. Data: {}", message);

        switch (eventType) {
            case "PAYMENT_CREATED" -> handleCreation(message);
            case "PAYMENT_UPDATED" -> handleUpdate(message);
            case "PAYMENT_DELETED" -> handleDelete(message);
            default -> log.warn("Unhandled event type: {}", eventType);
        }

    }

    @Override
    public List<String> getSupportedTypes() {
        return List.of("PAYMENT_CREATED", "PAYMENT_UPDATED", "PAYMENT_DELETED");
    }

    //TODO: implement all the events
    private void handleCreation(String message) { log.info("handleCreation: " + message);}
    private void handleUpdate(String message) { log.info("handleUpdate: " + message);}
    private void handleDelete(String message) { log.info("handleDelete: " + message);}
}
