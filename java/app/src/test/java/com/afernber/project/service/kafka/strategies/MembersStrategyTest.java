package com.afernber.project.service.kafka.strategies;

import com.afernber.project.constant.EmailConstants;
import com.afernber.project.constant.EventTypeConstants;
import com.afernber.project.domain.dto.MemberDTO;
import com.afernber.project.domain.entity.MemberEntity;
import com.afernber.project.exception.kafka.KafkaException;
import com.afernber.project.mappers.MemberMapper;
import com.afernber.project.repository.MemberRepository;
import com.afernber.project.service.impl.EmailServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembersStrategyTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberMapper mapper;
    @Mock
    private EmailServiceImpl emailService;

    private MembersStrategy strategy;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        strategy = new MembersStrategy(memberRepository, mapper, emailService);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void execute_ShouldProcessUserCreatedEvent_WhenMemberExistsInDb() throws JsonProcessingException {
        Long memberId = 1L;
        MemberDTO memberDTO = new MemberDTO(memberId, "John Doe", "john@example.com", LocalDateTime.now(), true, Collections.emptySet());
        String message = objectMapper.writeValueAsString(memberDTO);

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(memberId);
        memberEntity.setEmail("john@example.com");
        memberEntity.setName("John Doe");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(mapper.toDto(memberEntity)).thenReturn(memberDTO);

        
        strategy.execute(message, EventTypeConstants.USER_CREATED);

        verify(emailService).sendHtmlEmail(
                eq("john@example.com"),
                eq(EmailConstants.SUBJECT_ACTIVITY),
                eq(EmailConstants.TEMPLATE_GENERAL),
                any(Map.class),
                isNull(),
                isNull()
        );
    }

    @Test
    void execute_ShouldProcessUserCreatedEvent_WhenMemberDeletedFromDb() throws JsonProcessingException {
        Long memberId = 1L;
        MemberDTO memberDTO = new MemberDTO(memberId, "John Doe", "john@example.com", LocalDateTime.now(), true, Collections.emptySet());
        String message = objectMapper.writeValueAsString(memberDTO);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        
        strategy.execute(message, EventTypeConstants.USER_CREATED);

        verify(emailService).sendHtmlEmail(
                eq("john@example.com"),
                eq(EmailConstants.SUBJECT_ACTIVITY),
                eq(EmailConstants.TEMPLATE_GENERAL),
                any(Map.class),
                isNull(),
                isNull()
        );
    }

    @Test
    void execute_ShouldThrowKafkaException_WhenJsonIsInvalid() {
        String invalidMessage = "{invalid_json}";
        
        assertThrows(KafkaException.class, () ->
                strategy.execute(invalidMessage, EventTypeConstants.USER_CREATED)
        );
    }

    @Test
    void getSupportedTypes_ShouldReturnCorrectTypes() {
        var types = strategy.getSupportedTypes();
        
        assertTrue(types.contains(EventTypeConstants.USER_CREATED));
        assertTrue(types.contains(EventTypeConstants.USER_UPDATED));
        assertTrue(types.contains(EventTypeConstants.USER_DELETED));
        assertEquals(3, types.size());
    }
}