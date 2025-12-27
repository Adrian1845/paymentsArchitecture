package com.afernber.project.service;

import com.afernber.project.domain.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {

    PaymentDTO getPayment(Long id);

    List<PaymentDTO> getPayments();

    List<PaymentDTO> getPaymentsByMember(Long memberId);

    void createPayment(PaymentDTO member);

    PaymentDTO updatePayment(Long id, PaymentDTO memberDTO);

    void deletePayment(Long id);
}
