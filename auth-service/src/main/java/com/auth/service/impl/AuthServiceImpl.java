package com.auth.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.dto.AuthResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import com.auth.service.AuthService;
import com.auth.util.JwtUtil;

import com.auth.exception.InvalidCredentialsException;
import com.auth.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public String register(RegisterRequest request) {
        log.debug("Starting user registration for email: {}", request.getEmail());
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("Registration failed: User already exists with email: {}", request.getEmail());
            throw new UserAlreadyExistsException("User already exists with this email");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        
        log.info("User registered successfully with email: {}", request.getEmail());
        return "Registered Successfully";
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        log.debug("Starting login process for email: {}", request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed: User not found with email: {}", request.getEmail());
                    return new InvalidCredentialsException("Invalid credentials");
                });
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Login failed: Invalid password for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid credentials");
        }
        
        log.info("User logged in successfully with email: {}", request.getEmail());
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }
}
