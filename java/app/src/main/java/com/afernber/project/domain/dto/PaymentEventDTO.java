package com.afernber.project.domain.dto;

import java.math.BigDecimal;

public record PaymentEventDTO (Long paymentId,
                               Long userId,
                               String email,
                               BigDecimal amount
) {}
