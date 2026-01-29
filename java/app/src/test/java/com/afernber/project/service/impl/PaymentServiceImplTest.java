package com.afernber.project.service.impl;

import com.afernber.project.domain.dto.PaymentDTO;
import com.afernber.project.domain.entity.MemberEntity;
import com.afernber.project.domain.entity.PaymentEntity;
import com.afernber.project.mappers.PaymentMapper;
import com.afernber.project.repository.MemberRepository;
import com.afernber.project.repository.PaymentRepository;
import com.afernber.project.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository repository;
    @Mock private MemberRepository memberRepository;
    @Mock private PaymentMapper mapper;
    @Mock private KafkaProducerService producerService;
    @Mock private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks private PaymentServiceImpl service;

    @Test
    void getPayments_ShouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(new PaymentEntity()));
        when(mapper.toDto(any())).thenReturn(new PaymentDTO(1L, BigDecimal.TEN, "USD", 1L));

        List<PaymentDTO> result = service.getPayments();

        assertFalse(result.isEmpty());
    }

    @Test
    void createPayment_ShouldSavePayment() {
        PaymentDTO dto = new PaymentDTO(null, BigDecimal.TEN, "USD", 1L);
        PaymentEntity entity = new PaymentEntity();
        
        when(memberRepository.findById(1L)).thenReturn(Optional.of(new MemberEntity()));
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toDto(any())).thenReturn(new PaymentDTO(null, BigDecimal.TEN, "USD", 1L));

        service.createPayment(dto);

        verify(repository).save(any());
        verify(producerService).sendEvent(any(), any(), any(), any());
    }
}