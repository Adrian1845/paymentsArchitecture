package com.afernber.project.exception;

import com.afernber.project.domain.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(BaseProjectException.class)
    public ResponseEntity<ErrorResponse> handleDomainExceptions(BaseProjectException ex) {
        ErrorCatalog info = ex.getErrorInfo();

        ErrorResponse error = new ErrorResponse(
                info.getCode(),
                ex.getStandardMessage(),
                ex.getCustomMessage(),
                LocalDateTime.now()
        );

        log.warn("Domain Exception [{}]: {}", info.getCode(), ex.getMessage());
        return new ResponseEntity<>(error, info.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Unhandled System Exception: ", ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please contact support if the issue persists.",
                null,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

