package com.fingenie.ai.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.dto.LoginRequest;
import com.fingenie.ai.dto.LoginResponse;
import com.fingenie.ai.dto.RegisterRequest;
import com.fingenie.ai.dto.UserResponse;
import com.fingenie.ai.dto.VerifyOtpRequest;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.repository.UserRepository;
import com.fingenie.ai.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class AuthController {

	private final AuthService authService;
	private final UserRepository userRepository;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("User already exists");
		}

		UserResponse user = authService.register(request);

		return ResponseEntity.ok(new ApiResponse<>(200, "User registered successfully", user, LocalDateTime.now()));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {

		// ✅ call service
		LoginResponse response = authService.login(request);

		return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", response, LocalDateTime.now()));
	}

	@PostMapping("/verify")
	public ResponseEntity<ApiResponse<String>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {

		authService.verifyOtp(request);

		return ResponseEntity.ok(new ApiResponse<>(200, "OTP verified successfully", "Verified", LocalDateTime.now()));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout() {

		return ResponseEntity.ok(new ApiResponse<>(200, "Logged out successfully", "Logout done", LocalDateTime.now()));
	}

}
