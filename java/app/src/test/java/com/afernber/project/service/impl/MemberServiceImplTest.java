package com.afernber.project.service.impl;

import com.afernber.project.domain.dto.MemberDTO;
import com.afernber.project.domain.entity.MemberEntity;
import com.afernber.project.domain.entity.RoleEntity;
import com.afernber.project.mappers.MemberMapper;
import com.afernber.project.repository.MemberRepository;
import com.afernber.project.repository.PaymentRepository;
import com.afernber.project.repository.RoleRepository;
import com.afernber.project.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock private MemberRepository repository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private MemberMapper mapper;
    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private KafkaProducerService producerService;

    @InjectMocks private MemberServiceImpl service;

    @Test
    void getMembers_ShouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(new MemberEntity()));
        when(mapper.toDto(any())).thenReturn(new MemberDTO(1L, "Test", "test@test.com", null, true, null));

        List<MemberDTO> result = service.getMembers();

        assertFalse(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void createMember_ShouldSaveMember() {
        MemberDTO dto = new MemberDTO(null, "Test", "test@test.com", null, true, null);
        MemberEntity entity = new MemberEntity();
        
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new RoleEntity(1, "ROLE_USER")));
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toDto(any())).thenReturn(new MemberDTO(1L, "Test", "test@test.com", null, true, null));

        service.createMember(dto);

        verify(repository).save(any());
        verify(producerService).sendEvent(any(), any(), any(), any());
    }
}