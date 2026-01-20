package com.afernber.project.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCatalog {
    int getCode();
    HttpStatus getStatus();

    String getMessage();
}
