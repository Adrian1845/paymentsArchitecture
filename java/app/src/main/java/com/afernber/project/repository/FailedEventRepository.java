package com.afernber.project.repository;

import com.afernber.project.domain.entity.FailedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedEventRepository extends JpaRepository<FailedEventEntity, Long> {
    List<FailedEventEntity> findByStatus(String status);
}
