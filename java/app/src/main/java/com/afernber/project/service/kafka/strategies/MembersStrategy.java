package com.afernber.project.service.kafka.strategies;

import com.afernber.project.constant.EmailConstants;
import com.afernber.project.constant.EventTypeConstants;
import com.afernber.project.domain.dto.MemberDTO;
import com.afernber.project.mappers.MemberMapper;
import com.afernber.project.repository.MemberRepository;
import com.afernber.project.service.impl.EmailServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class MembersStrategy implements KafkaStrategy {

    private final MemberRepository memberRepository;
    private final MemberMapper mapper;
    private final EmailServiceImpl emailService;

    @Override
    public void execute(String message, String eventType) {
        log.info("👤 Strategy: Processing User Creation. Data: {}", message);
        handleEvent(message, eventType);
    }

    @Override
    public List<String> getSupportedTypes() {
        return List.of(EventTypeConstants.USER_CREATED, EventTypeConstants.USER_UPDATED, EventTypeConstants.USER_DELETED);
    }


    private static final Map<String, String> ACTIVITY_MESSAGES = Map.of(
            EventTypeConstants.USER_CREATED, EmailConstants.CREATE_USER_ACTIVITY,
            EventTypeConstants.USER_UPDATED, EmailConstants.UPDATE_USER_ACTIVITY,
            EventTypeConstants.USER_DELETED, EmailConstants.DELETED_USER_ACTIVITY
    );

    private void handleEvent(String message, String eventType) {
        String activityMessage = ACTIVITY_MESSAGES.getOrDefault(eventType, "System update performed");
        processMemberActivity(message, eventType, activityMessage);
    }

    private void processMemberActivity(String message, String actionName, String emailMessageContent) {
        log.info("handle {}: {}", actionName, message);

        MemberDTO memberDTO = findMember(message);

        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.RECIPIENT_NAME, memberDTO.name());
        model.put(EmailConstants.MESSAGE, emailMessageContent);

        notify(memberDTO.email(), model);
    }

    private void notify(String to, Map<String, Object> model) {
        model.putIfAbsent(EmailConstants.TITLE, EmailConstants.SUBJECT_ACTIVITY);
        model.putIfAbsent(EmailConstants.BUSINESS_NAME, EmailConstants.BUSINESS);

        emailService.sendHtmlEmail(
                to,
                EmailConstants.SUBJECT_ACTIVITY,
                EmailConstants.TEMPLATE_GENERAL,
                model,
                null,
                null
        );
    }

    private MemberDTO findMember(String message) {
        ObjectMapper om = new ObjectMapper();
        try {
            MemberDTO eventData = om.readValue(message, MemberDTO.class);

            return memberRepository.findById(eventData.id())
                    .map(mapper::toDto)
                    .orElseGet(() -> {
                        log.info("Member {} already deleted from DB. Using data from Kafka payload.", eventData.id());
                        return eventData;
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Kafka message: {}", message);
            throw new RuntimeException("Json Error", e);
        }
    }
}