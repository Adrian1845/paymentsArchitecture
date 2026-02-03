package com.afernber.project.domain.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record MemberDTO(
        Long id,
        String name,
        String email,
        LocalDateTime createdAt,
        Boolean enabled,
        Set<String> roles
) {}