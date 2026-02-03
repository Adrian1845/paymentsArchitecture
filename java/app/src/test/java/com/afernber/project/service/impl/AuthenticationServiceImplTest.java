package com.afernber.project.service.impl;

import com.afernber.project.domain.entity.MemberEntity;
import com.afernber.project.domain.entity.RoleEntity;
import com.afernber.project.domain.request.LoginRequest;
import com.afernber.project.domain.request.RegisterRequest;
import com.afernber.project.domain.response.AuthResponse;
import com.afernber.project.repository.MemberRepository;
import com.afernber.project.repository.RoleRepository;
import com.afernber.project.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock private MemberRepository repository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthenticationServiceImpl service;

    @Test
    void register_ShouldReturnToken() {
        RegisterRequest req = new RegisterRequest("John", "Doe", "john@test.com", "pass");
        MemberEntity savedMember = new MemberEntity();
        savedMember.setEmail("john@test.com");
        savedMember.setRoles(Set.of(new RoleEntity(1, "ROLE_USER")));

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new RoleEntity(1, "ROLE_USER")));
        when(repository.save(any())).thenReturn(savedMember);
        when(jwtUtils.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = service.register(req);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
    }

    @Test
    void authenticate_ShouldReturnToken() {
        LoginRequest req = new LoginRequest("john@test.com", "pass");
        MemberEntity member = new MemberEntity();
        member.setEmail("john@test.com");
        member.setRoles(Set.of(new RoleEntity(1, "ROLE_USER")));

        when(repository.findByEmail(req.email())).thenReturn(Optional.of(member));
        when(jwtUtils.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = service.authenticate(req);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        verify(authenticationManager).authenticate(any());
    }
}