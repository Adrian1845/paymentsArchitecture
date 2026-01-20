package com.afernber.project.exception.kafka;

import com.afernber.project.exception.BaseProjectException;

public class KafkaException extends BaseProjectException {
    public KafkaException(KafkaErrorCode error) { super(error); }
    public KafkaException(KafkaErrorCode error, String message) { super(error, message); }
}
