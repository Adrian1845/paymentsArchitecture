package com.afernber.project.service;

public interface KafkaProducerService {
    void sendEvent(String topic, String payload, String eventType, Long originalId);
}
