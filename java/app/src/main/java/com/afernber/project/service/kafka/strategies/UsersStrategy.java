package com.afernber.project.service.kafka.strategies;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UsersStrategy implements KafkaStrategy {
    @Override
    public void execute(String message, String eventType) {
        log.info("👤 Strategy: Processing User Creation. Data: {}", message);

        // We use a Switch because we only want to make 3 events
        // THIS IS NOT A SCALABLE SOLUTION :)
        switch (eventType) {
            case "USER_CREATED" -> handleCreation(message);
            case "USER_UPDATED" -> handleUpdate(message);
            case "USER_DELETED" -> handleDelete(message);
            default -> throw new RuntimeException("Simulated Error to test DLT");
        }
    }

    @Override
    public List<String> getSupportedTypes() {
        return List.of("USER_CREATED", "USER_UPDATED", "USER_DELETED");
    }

    //TODO: implement all the events
    private void handleCreation(String message) {
        log.info("handleCreation: " + message);
    }
    private void handleUpdate(String message) { log.info("handleUpdate: " + message);}
    private void handleDelete(String message) { log.info("handleDelete: " + message);}
}