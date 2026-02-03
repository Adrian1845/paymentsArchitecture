package com.afernber.project.service;

import com.afernber.project.domain.request.LoginRequest;
import com.afernber.project.domain.request.RegisterRequest;
import com.afernber.project.domain.response.AuthResponse;

public interface AuthenticationService {
    AuthResponse register(RegisterRequest request);

    AuthResponse authenticate(LoginRequest request);
}