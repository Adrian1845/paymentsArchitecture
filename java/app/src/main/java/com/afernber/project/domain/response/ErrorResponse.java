package com.afernber.project.domain.response;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String message, String customMessage, LocalDateTime timestamp) {
}
