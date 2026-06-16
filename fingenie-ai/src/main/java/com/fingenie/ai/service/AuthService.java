package com.fingenie.ai.service;

import com.fingenie.ai.dto.LoginRequest;
import com.fingenie.ai.dto.LoginResponse;
import com.fingenie.ai.dto.RegisterRequest;
import com.fingenie.ai.dto.UserResponse;
import com.fingenie.ai.dto.VerifyOtpRequest;

import jakarta.validation.Valid;

public interface AuthService {

	UserResponse register(@Valid RegisterRequest request);

	LoginResponse login(LoginRequest request);

	void verifyOtp(VerifyOtpRequest request);
	
}
