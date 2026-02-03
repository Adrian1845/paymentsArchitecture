package com.afernber.project.domain.request;

public record LoginRequest(
        String email,
        String password
) {}
