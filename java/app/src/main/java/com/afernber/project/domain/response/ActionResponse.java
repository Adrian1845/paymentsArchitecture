package com.afernber.project.domain.response;

import java.time.LocalDateTime;

public record ActionResponse(
        String message,
        LocalDateTime timestamp
) {}
