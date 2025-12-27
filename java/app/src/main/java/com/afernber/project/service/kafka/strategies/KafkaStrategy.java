package com.afernber.project.service.kafka.strategies;

import java.util.List;

public interface KafkaStrategy {
    void execute(String message, String eventType   );
    List<String> getSupportedTypes(); // Identifies if it's 'USER_CREATED' or 'PAYMENT_MADE'
}
