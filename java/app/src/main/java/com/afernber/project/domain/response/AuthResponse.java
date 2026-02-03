package com.afernber.project.domain.response;

import java.util.Set;

public record AuthResponse(
        String token,
        String email,
        Set<String> roles
) {}
