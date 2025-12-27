package com.afernber.project.repository;

import com.afernber.project.domain.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    boolean existsByMemberId(Long memberId);

    List<PaymentEntity> findByMemberId(Long memberId);
}
