package com.auth.service;

import com.auth.dto.AuthResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;

public interface AuthService {
	String register(RegisterRequest request);
    AuthResponse login(LoginRequest request);

}
