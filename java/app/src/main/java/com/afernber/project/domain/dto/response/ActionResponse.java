package com.afernber.project.domain.dto.response;

import java.time.LocalDateTime;

public record ActionResponse(
        String message,
        LocalDateTime timestamp
) {}
