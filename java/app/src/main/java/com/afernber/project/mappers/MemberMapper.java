package com.afernber.project.mappers;

import com.afernber.project.domain.dto.MemberDTO;
import com.afernber.project.domain.entity.MemberEntity;
import com.afernber.project.domain.entity.RoleEntity;
import java.util.Collections;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    MemberDTO toDto(MemberEntity entity);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    MemberEntity toEntity(MemberDTO dto);

    @Named("mapRoles")
    default Set<String> mapRoles(Set<RoleEntity> roles) {
        if (roles == null) return Collections.emptySet();
        return roles.stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
    }
}