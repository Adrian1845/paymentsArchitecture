package com.afernber.project.exception.payment;

import com.afernber.project.exception.BaseProjectException;

public class PaymentException extends BaseProjectException {
    public PaymentException(PaymentErrorCode error) { super(error); }
    public PaymentException(PaymentErrorCode error, String message) { super(error, message); }
}
