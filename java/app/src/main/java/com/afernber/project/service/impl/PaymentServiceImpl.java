package com.afernber.project.service.impl;

import com.afernber.project.domain.dto.PaymentDTO;
import com.afernber.project.domain.entity.MemberEntity;
import com.afernber.project.domain.entity.PaymentEntity;
import com.afernber.project.helpers.LatencyHelper;
import com.afernber.project.mappers.PaymentMapper;
import com.afernber.project.repository.MemberRepository;
import com.afernber.project.repository.PaymentRepository;
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

@Service
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final PaymentMapper mapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_MEMBER_PAYMENTS = "memberPayments:";
    @Override
    public PaymentDTO getPayment(Long id) {
        return paymentRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
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
        String key = CACHE_KEY_MEMBER_PAYMENTS + memberId;
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
        MemberEntity member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        PaymentEntity entity = mapper.toEntity(dto);
        entity.setMember(member);

        if (entity.getId() != null) {
            log.info("Manual ID {} provided. Setting to null to let DB generate ID and avoid StaleObject error.", dto.id());
            entity.setId(null);
        }

        paymentRepository.save(entity);

        evictMemberPaymentsCache(dto.memberId());

        log.info("Payment created and cache evicted for member: {}", dto.memberId());
    }

    @Override
    @Transactional
    public PaymentDTO updatePayment(Long id, PaymentDTO dto) {
        PaymentEntity existing = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        existing.setAmount(dto.amount());
        existing.setCurrency(dto.currency());

        if (!existing.getMember().getId().equals(dto.memberId())) {
            MemberEntity newMember = memberRepository.findById(dto.memberId())
                    .orElseThrow(() -> new RuntimeException("New member not found"));
            existing.setMember(newMember);
        }

        PaymentEntity updated = paymentRepository.save(existing);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        PaymentEntity payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Long memberId = payment.getMember().getId();
        paymentRepository.delete(payment);

        evictMemberPaymentsCache(memberId);
    }

    private void evictMemberPaymentsCache(Long memberId) {
        redisTemplate.delete(CACHE_KEY_MEMBER_PAYMENTS + memberId);
    }
}
