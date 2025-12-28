package com.afernber.project.service.impl;

import com.afernber.project.domain.dto.EventDTO;
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

@AllArgsConstructor
@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;
    private final PaymentRepository paymentRepository;

    private final MemberMapper mapper;

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY_PREFIX = "memberRedis:";

    private final KafkaProducerService producerService;

    @Override
    public MemberDTO getMember(Long id) {
        String key = CACHE_KEY_PREFIX + id;

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

        EventDTO event = new EventDTO(memberEntity.getId());

        producerService.sendEvent("payments-events-topic", JsonHelper.toJson(event), "USER_CREATED", null);
    }

    @Override
    @Transactional
    public MemberDTO updateMember(Long id, MemberDTO dto) {
        MemberEntity existing = validateUserExists(id);

        existing.setName(dto.name());
        existing.setEmail(dto.email());

        MemberEntity updated = repository.save(existing);
        evictCache(id);
        log.info("Updated member in DB: {}", id);

        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        validateUserExists(id);

        boolean hasPayments = paymentRepository.existsByMemberId(id);

        if (hasPayments) {
            throw new RuntimeException("Cannot delete member: This member has existing payment history.");
        }

        repository.deleteById(id);
        evictCache(id);

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
        redisTemplate.delete(CACHE_KEY_PREFIX + id);
    }
}