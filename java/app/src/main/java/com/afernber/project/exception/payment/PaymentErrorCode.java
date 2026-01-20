package com.afernber.project.exception.payment;

import com.afernber.project.exception.ErrorCatalog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCatalog {
    INSUFFICIENT_FUNDS(2001, HttpStatus.PAYMENT_REQUIRED, "Balance too low"),
    PAYMENT_NOT_FOUND(2002, HttpStatus.NOT_FOUND, "Payment record missing");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
