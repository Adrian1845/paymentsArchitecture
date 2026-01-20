package com.afernber.project.service.impl;

import com.afernber.project.constant.EventTypeConstants;
import com.afernber.project.constant.ExceptionConstants;
import com.afernber.project.constant.KafkaConstants;
import com.afernber.project.constant.RedisConstants;
import com.afernber.project.domain.dto.PaymentDTO;
import com.afernber.project.domain.dto.PaymentEventDTO;
import com.afernber.project.domain.entity.MemberEntity;
import com.afernber.project.domain.entity.PaymentEntity;
import com.afernber.project.exception.member.MemberErrorCode;
import com.afernber.project.exception.member.MemberException;
import com.afernber.project.exception.payment.PaymentErrorCode;
import com.afernber.project.exception.payment.PaymentException;
import com.afernber.project.helpers.JsonHelper;
import com.afernber.project.helpers.LatencyHelper;
import com.afernber.project.mappers.PaymentMapper;
import com.afernber.project.repository.MemberRepository;
import com.afernber.project.repository.PaymentRepository;
import com.afernber.project.service.KafkaProducerService;
import com.afernber.project.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final PaymentMapper mapper;

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaProducerService producerService;

    @Override
    public PaymentDTO getPayment(Long id) {
        return paymentRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new PaymentException(
                        PaymentErrorCode.PAYMENT_NOT_FOUND,
                        String.format(ExceptionConstants.NOT_FOUND_MSG, ExceptionConstants.PAYMENT_MSG, id)
                ));
    }

    @Override
    public List<PaymentDTO> getPayments() {
        return paymentRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByMember(Long memberId) {
        String key = RedisConstants.PAYMENT_REDIS + memberId;
        Object rawData = redisTemplate.opsForValue().get(key);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        if (rawData instanceof String jsonString) {
            try {
                log.info("Redis HIT for Member Payments: {}", memberId);
                return objectMapper.readValue(jsonString, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                log.error("Failed to parse JSON from Redis: {}", e.getMessage());
            }
        }

        log.warn("Redis MISS for Member Payments: {}. Fetching from DB...", memberId);
        LatencyHelper.simulateLatency();

        List<PaymentEntity> entities = paymentRepository.findByMemberId(memberId);
        List<PaymentDTO> dtos = entities.stream().map(mapper::toDto).toList();

        if (!dtos.isEmpty()) {
            try {
                String jsonToCache = objectMapper.writeValueAsString(dtos);
                redisTemplate.opsForValue().set(key, jsonToCache, Duration.ofMinutes(5));
            } catch (JsonProcessingException e) {
                log.error("❌ Failed to cache payments: {}", e.getMessage());
            }
        }

        return dtos;
    }

    @Override
    @Transactional
    public void createPayment(PaymentDTO dto) {
        MemberEntity member = findMember(dto.memberId());

        PaymentEntity entity = mapper.toEntity(dto);
        entity.setMember(member);

        if (entity.getId() != null) {
            log.info("Manual ID {} provided. Setting to null to let DB generate ID and avoid StaleObject error.", dto.id());
            entity.setId(null);
        }

        PaymentDTO saved = mapper.toDto(paymentRepository.save(entity));

        evictMemberPaymentsCache(dto.memberId());

        producerService.sendEvent(KafkaConstants.PAYMENTS_TOPIC,
                JsonHelper.toJson(buildPaymentEvent(saved, member)),
                EventTypeConstants.PAYMENT_CREATED,
                null
        );
        log.info("Payment created and cache evicted for member: {}", dto.memberId());
    }



    @Override
    @Transactional
    public PaymentDTO updatePayment(Long id, PaymentDTO dto) {
        PaymentEntity existing = findPayment(id);

        Optional.ofNullable(dto.amount()).ifPresent(existing::setAmount);
        Optional.ofNullable(dto.currency())
                .filter(c -> !c.isBlank())
                .ifPresent(existing::setCurrency);

        Long newMemberId = dto.memberId();
        Long currentMemberId = Optional.ofNullable(existing.getMember())
                .map(MemberEntity::getId)
                .orElse(null);

        if (newMemberId != null && !newMemberId.equals(currentMemberId)) {
            MemberEntity newMember = findMember(newMemberId);
            existing.setMember(newMember);
        }

        PaymentEntity updated = paymentRepository.save(existing);

        evictMemberPaymentsCache(currentMemberId);
        if (newMemberId != null && !newMemberId.equals(currentMemberId)) {
            evictMemberPaymentsCache(newMemberId);
        }

        PaymentDTO updatedDto = mapper.toDto(updated);
        MemberEntity finalMember = updated.getMember();

        producerService.sendEvent(
                KafkaConstants.PAYMENTS_TOPIC,
                JsonHelper.toJson(buildPaymentEvent(updatedDto, finalMember)),
                EventTypeConstants.PAYMENT_UPDATED,
                null
        );

        return updatedDto;
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        PaymentEntity payment = findPayment(id);

        Long memberId = payment.getMember().getId();
        paymentRepository.delete(payment);
        PaymentDTO dto = mapper.toDto(payment);

        MemberEntity member = findMember(dto.memberId());

        producerService.sendEvent(KafkaConstants.PAYMENTS_TOPIC,
                JsonHelper.toJson(buildPaymentEvent(dto, member)),
                EventTypeConstants.PAYMENT_DELETED,
                null
        );

        evictMemberPaymentsCache(memberId);
    }

    private PaymentEntity findPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentException(
                        PaymentErrorCode.PAYMENT_NOT_FOUND,
                        String.format(ExceptionConstants.NOT_FOUND_MSG, ExceptionConstants.PAYMENT_MSG, id)
                ));
    }

    private MemberEntity findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(
                        MemberErrorCode.MEMBER_NOT_FOUND,
                        String.format(ExceptionConstants.NOT_FOUND_MSG, ExceptionConstants.MEMBER_MSG, id)
                ));
    }

    private PaymentEventDTO buildPaymentEvent(PaymentDTO dto, MemberEntity member) {
        return new PaymentEventDTO(dto.id(), dto.memberId(), member.getEmail(), dto.amount());
    }

    private void evictMemberPaymentsCache(Long memberId) {
        redisTemplate.delete(RedisConstants.PAYMENT_REDIS + memberId);
    }
}
