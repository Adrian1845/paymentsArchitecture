package com.afernber.project.service.impl;

import com.afernber.project.domain.entity.MemberEntity;
import com.afernber.project.domain.entity.RoleEntity;
import com.afernber.project.domain.request.LoginRequest;
import com.afernber.project.domain.request.RegisterRequest;
import com.afernber.project.domain.response.AuthResponse;
import com.afernber.project.repository.MemberRepository;
import com.afernber.project.repository.RoleRepository;
import com.afernber.project.security.JwtUtils;
import com.afernber.project.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final MemberRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        var user = new MemberEntity();
        user.setName(request.firstname() + " " + request.lastname());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        
        Set<RoleEntity> roles = new HashSet<>();
        // Default role is ROLE_USER
        RoleEntity userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        
        user.setRoles(roles);

        repository.save(user);
        var jwtToken = jwtUtils.generateToken(user);
        return new AuthResponse(
                jwtToken,
                user.getEmail(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = repository.findByEmail(request.email())
                .orElseThrow();
        var jwtToken = jwtUtils.generateToken(user);
        return new AuthResponse(
                jwtToken,
                user.getEmail(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())
        );
    }
}