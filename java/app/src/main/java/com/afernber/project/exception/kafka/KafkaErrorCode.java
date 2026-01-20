package com.afernber.project.exception.kafka;

import com.afernber.project.exception.ErrorCatalog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum KafkaErrorCode implements ErrorCatalog {
    EVENT_TYPE_NOT_SUPPORTED(3001, HttpStatus.INTERNAL_SERVER_ERROR, "The event type is not supported"),
    CANNOT_SEND_EMAIL(3002, HttpStatus.INTERNAL_SERVER_ERROR, "The email could not be sent"),
    COULD_NOT_PROCESS_JSON(3003, HttpStatus.INTERNAL_SERVER_ERROR, "Could not process the JSON");

    private final int code;
    private final HttpStatus status;
    private final String message;

}
