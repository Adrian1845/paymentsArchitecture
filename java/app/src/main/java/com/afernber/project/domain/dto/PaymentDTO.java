package com.afernber.project.domain.dto;

import java.math.BigDecimal;

public record PaymentDTO(
        Long id,
        BigDecimal amount,
        String currency,
        Long memberId
) {}
