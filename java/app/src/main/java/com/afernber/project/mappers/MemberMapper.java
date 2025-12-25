package com.afernber.project.mappers;

import com.afernber.project.domain.dto.MemberDTO;
import com.afernber.project.domain.entity.MemberEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberDTO toDto(MemberEntity entity);

    MemberEntity toEntity(MemberDTO dto);
}
