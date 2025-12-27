package com.afernber.project.mappers;

import com.afernber.project.domain.dto.PaymentDTO;
import com.afernber.project.domain.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "member.id", target = "memberId")
    PaymentDTO toDto(PaymentEntity entity);

    @Mapping(target = "member", ignore = true)
    PaymentEntity toEntity(PaymentDTO dto);

}
