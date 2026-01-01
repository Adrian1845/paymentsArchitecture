package com.afernber.project.service;

public interface FailedEventService {

    void saveOrUpdateFailedEvent(String message, String topic, String exceptionMessage, String eventType, Long existingId);

    void replayEvent(Long id);

    void replayAllEvents();
}
