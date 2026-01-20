package com.afernber.project.exception;

import lombok.Getter;

@Getter
public abstract class BaseProjectException extends RuntimeException {
    private final ErrorCatalog errorInfo;
    private final String standardMessage;
    private final String customMessage;

    protected BaseProjectException(ErrorCatalog errorInfo) {
        super(errorInfo.getMessage());
        this.errorInfo = errorInfo;
        this.standardMessage = errorInfo.getMessage();
        this.customMessage = errorInfo.getMessage();
    }

    protected BaseProjectException(ErrorCatalog errorInfo, String customMessage) {
        super(customMessage);
        this.errorInfo = errorInfo;
        this.standardMessage = errorInfo.getMessage();
        this.customMessage = customMessage;
    }
}
