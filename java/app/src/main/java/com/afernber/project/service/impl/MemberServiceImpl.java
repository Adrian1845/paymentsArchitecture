package com.afernber.project.service.impl;

import com.afernber.project.constant.EventTypeConstants;
import com.afernber.project.constant.KafkaConstants;
import com.afernber.project.constant.RedisConstants;
import com.afernber.project.domain.dto.MemberDTO;
import com.afernber.project.domain.entity.MemberEntity;
import com.afernber.project.helpers.JsonHelper;
import com.afernber.project.helpers.LatencyHelper;
import com.afernber.project.mappers.MemberMapper;
import com.afernber.project.repository.MemberRepository;
import com.afernber.project.repository.PaymentRepository;
import com.afernber.project.service.KafkaProducerService;
import com.afernber.project.service.MemberService;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;
    private final PaymentRepository paymentRepository;
    private final MemberMapper mapper;

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaProducerService producerService;

    @Override
    public MemberDTO getMember(Long id) {
        String key = RedisConstants.MEMBER_REDIS + id;

        MemberDTO cachedMember = (MemberDTO) redisTemplate.opsForValue().get(key);
        if (cachedMember != null) {
            log.info("Redis HIT for Member ID: {}", id);
            return cachedMember;
        }

        log.warn("Redis MISS for Member ID: {}. Fetching from DB...", id);
        LatencyHelper.simulateLatency();

        MemberEntity member = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));

        MemberDTO dto = mapper.toDto(member);

        redisTemplate.opsForValue().set(key, dto, Duration.ofMinutes(10));
        log.info("Member ID: {} saved to Redis", id);

        return dto;
    }

    @Override
    public List<MemberDTO> getMembers() {
        LatencyHelper.simulateLatency();

        List<MemberEntity> members = repository.findAll();

        return members.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void createMember(MemberDTO member) {
        if (member == null) {
            throw new IllegalArgumentException("Member data cannot be null");
        }

        MemberEntity memberEntity = mapper.toEntity(member);

        if (memberEntity.getId() != null) {
            log.info("Manual ID {} provided. Setting to null to let DB generate ID and avoid StaleObject error.", member.id());
            memberEntity.setId(null);
        }

        repository.save(memberEntity);
        log.info("Created member in DB: {}", member);

        producerService.sendEvent(KafkaConstants.PAYMENTS_TOPIC,
                JsonHelper.toJson(member.id()),
                EventTypeConstants.USER_CREATED,
                null
        );
    }

    @Override
    @Transactional
    public MemberDTO updateMember(Long id, MemberDTO dto) {
        MemberEntity existing = validateUserExists(id);

        Optional.ofNullable(dto.name())
                .filter(name -> !name.isBlank())
                .ifPresent(existing::setName);

        Optional.ofNullable(dto.email())
                .filter(email -> !email.isBlank())
                .ifPresent(existing::setEmail);

        MemberEntity updated = repository.save(existing);
        evictCache(id);
        log.info("Updated member in DB: {}", id);

        producerService.sendEvent(KafkaConstants.PAYMENTS_TOPIC,
                JsonHelper.toJson(id),
                EventTypeConstants.USER_UPDATED,
                null)
        ;
        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        MemberDTO member = mapper.toDto(validateUserExists(id));

        boolean hasPayments = paymentRepository.existsByMemberId(id);

        if (hasPayments) {
            throw new RuntimeException("Cannot delete member: This member has existing payment history.");
        }

        repository.deleteById(id);
        evictCache(id);

        producerService.sendEvent(KafkaConstants.PAYMENTS_TOPIC,
                JsonHelper.toJson(member),
                EventTypeConstants.USER_DELETED,
                null
        );

        log.info("🗑Deleted member from DB: {}", id);
    }

    /*** Check if it exists before deleting to avoid silent failures
     *
     * @param id member id to check in db
     * @return MemberEntity entity of the member in case is needed
     */
    private MemberEntity validateUserExists(Long id) {
        log.info("Check if member exists in DB: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    private void evictCache(Long id) {
        redisTemplate.delete(RedisConstants.MEMBER_REDIS + id);
    }
}