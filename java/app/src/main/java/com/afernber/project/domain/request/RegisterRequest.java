package com.afernber.project.domain.request;

public record RegisterRequest(
        String firstname,
        String lastname,
        String email,
        String password
) {}