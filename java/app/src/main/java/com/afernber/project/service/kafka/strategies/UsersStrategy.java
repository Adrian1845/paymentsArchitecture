package com.afernber.project.service.kafka.strategies;

import com.afernber.project.domain.dto.MemberDTO;
import com.afernber.project.mappers.MemberMapper;
import com.afernber.project.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UsersStrategy implements KafkaStrategy {

    private MemberRepository memberRepository;
    private MemberMapper mapper;

    @Override
    public void execute(String message, String eventType) {
        log.info("👤 Strategy: Processing User Creation. Data: {}", message);

        // We use a Switch because we only want to make 3 events
        // THIS IS NOT A SCALABLE SOLUTION :)
        switch (eventType) {
            case "USER_CREATED" -> handleCreation(message);
            case "USER_UPDATED" -> handleUpdate(message);
            case "USER_DELETED" -> handleDelete(message);
            default -> throw new RuntimeException("Simulated Error to test DLT");
        }
    }

    @Override
    public List<String> getSupportedTypes() {
        return List.of("USER_CREATED", "USER_UPDATED", "USER_DELETED");
    }

    private void handleCreation(String message) {
        log.info("handleCreation: " + message);
        MemberDTO memberDTO = findMember(message);
    }

    private void handleUpdate(String message) {
        log.info("handleUpdate: " + message);
        MemberDTO memberDTO = findMember(message);
    }

    private void handleDelete(String message) {
        log.info("handleDelete: " + message);
        MemberDTO memberDTO = findMember(message);
    }

    private MemberDTO findMember(String message) {
        ObjectMapper om = new ObjectMapper();
        try {
            MemberDTO memberEvent = om.readValue(message, MemberDTO.class);
            log.info(String.valueOf(memberEvent));
            return mapper.toDto(memberRepository.findById(memberEvent.id())
                    .orElseThrow(() -> new RuntimeException("Member not found")));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}