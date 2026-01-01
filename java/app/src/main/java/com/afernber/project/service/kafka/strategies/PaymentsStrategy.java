package com.afernber.project.service.kafka.strategies;

import com.afernber.project.constant.EmailConstants;
import com.afernber.project.constant.EventTypeConstants;
import com.afernber.project.domain.dto.MemberDTO;
import com.afernber.project.domain.dto.PaymentDTO;
import com.afernber.project.domain.dto.PaymentEventDTO;
import com.afernber.project.mappers.MemberMapper;
import com.afernber.project.mappers.PaymentMapper;
import com.afernber.project.repository.MemberRepository;
import com.afernber.project.repository.PaymentRepository;
import com.afernber.project.service.impl.EmailServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class PaymentsStrategy implements KafkaStrategy {

    private final MemberRepository memberRepository;
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final MemberMapper memberMapper;
    private final EmailServiceImpl emailService;

    @Override
    public void execute(String message, String eventType) {
        log.info("👤 Strategy: Processing User Creation. Data: {}", message);
        handleEvent(message, eventType);
    }

    @Override
    public List<String> getSupportedTypes() {
        return List.of(EventTypeConstants.PAYMENT_CREATED, EventTypeConstants.PAYMENT_UPDATED, EventTypeConstants.PAYMENT_DELETED);
    }

    private static final Map<String, String> ACTIVITY_MESSAGES = Map.of(
            EventTypeConstants.PAYMENT_CREATED, EmailConstants.CREATE_PAYMENT_ACTIVITY,
            EventTypeConstants.PAYMENT_UPDATED, EmailConstants.UPDATE_PAYMENT_ACTIVITY,
            EventTypeConstants.PAYMENT_DELETED, EmailConstants.DELETED_PAYMENT_ACTIVITY
    );

    private void handleEvent(String message, String eventType) {
        String activityMessage = ACTIVITY_MESSAGES.getOrDefault(eventType, "System update performed");
        processPaymentActivity(message, eventType, activityMessage);
    }

    private void processPaymentActivity(String message, String actionName, String emailMessageContent) {
        log.info("Processing Payment {}: {}", actionName, message);

        MemberDTO memberDTO = findMemberFromPayment(message);
        PaymentDTO paymentDTO = findPayment(message);

        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.RECIPIENT_NAME, memberDTO.name());
        model.put(EmailConstants.MESSAGE, emailMessageContent);

        model.put(EmailConstants.TRANSACTION_ID, paymentDTO.id().toString());
        model.put(EmailConstants.AMOUNT, "$" + paymentDTO.amount());

        notify(memberDTO.email(), model);
    }

    private void notify(String to, Map<String, Object> model) {
        model.put(EmailConstants.BUSINESS_NAME, EmailConstants.BUSINESS);

        model.put(EmailConstants.DATE, LocalDate.now().toString());

        emailService.sendHtmlEmail(
                to,
                EmailConstants.SUBJECT_PAYMENT_RECEIPT,
                EmailConstants.TEMPLATE_PAYMENT,
                model,
                null,
                null
        );
    }

    /**
     * Extracts the userId from the Payment message and fetches the Member details
     */
    private MemberDTO findMemberFromPayment(String message) {
        PaymentEventDTO dto = getPaymentEvent(message);
        return memberRepository.findById(dto.userId())
                .map(memberMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Cannot send payment email: Member ID " + dto.userId() + " not found."));
    }

    private PaymentDTO findPayment(String message){
        PaymentEventDTO dto = getPaymentEvent(message);
        return repository.findById(dto.userId())
                .map(mapper::toDto)
                .orElseGet(() -> {
                    log.info("Payment {} already deleted from DB. Using data from Kafka payload.", dto.paymentId());
                    return new PaymentDTO(dto.paymentId(), dto.amount(), null, null);
                });
    }

    private PaymentEventDTO getPaymentEvent(String message) {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(message, PaymentEventDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Payment Kafka message: {}", message);
            throw new RuntimeException("Json Error", e);
        }
    }
}
