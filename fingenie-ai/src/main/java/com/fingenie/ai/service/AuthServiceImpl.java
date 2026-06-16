package com.fingenie.ai.service;


import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fingenie.ai.dto.LoginRequest;
import com.fingenie.ai.dto.LoginResponse;
import com.fingenie.ai.dto.RegisterRequest;
import com.fingenie.ai.dto.UserResponse;
import com.fingenie.ai.dto.VerifyOtpRequest;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.enums.Role;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.exception.ResourceNotFoundException;
import com.fingenie.ai.repository.UserRepository;
import com.fingenie.ai.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final EmailService emailService; // ✅ added

	private static final SecureRandom OTP_RANDOM = new SecureRandom();

	@Value("${app.otp.expiration-minutes}")
	private long otpExpirationMinutes;

	public UserResponse register(RegisterRequest request) {

	    // ✅ Validate all fields
	    if (request.getName() == null || request.getName().isBlank()) {
	        throw new BusinessException("Name is required");
	    }

	    if (request.getEmail() == null || request.getEmail().isBlank()) {
	        throw new BusinessException("Email is required");
	    }

	    if (request.getPassword() == null || request.getPassword().isBlank()) {
	        throw new BusinessException("Password is required");
	    }

	    // ✅ Email format check
	    if (!request.getEmail().contains("@")) {
	        throw new BusinessException("Invalid email format");
	    }

	    // ✅ Only email must be unique
	    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
	        throw new BusinessException("Email already registered. Please login.");
	    }

	    User user = User.builder()
	            .name(request.getName())
	            .email(request.getEmail())
	            .password(passwordEncoder.encode(request.getPassword()))
	            .role(Role.CUSTOMER)
	            .balance(0.0)
	            .build();

	    // OTP
	    String otp = generateOtp();
	    user.setOtp(otp);
	    user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
	    user.setVerified(false);

	    User savedUser = userRepository.save(user);

	    // safety check
	    if (savedUser.getEmail() == null || savedUser.getEmail().isBlank()) {
	        throw new BusinessException("Invalid email provided");
	    }

	    try {
	        emailService.sendEmail(savedUser.getEmail(), "Your FinGenie OTP is: " + otp);
	    } catch (Exception e) {
	        log.error("Email sending failed: {}", e.getMessage());
	        throw new BusinessException("Failed to send OTP email");
	    }

	    return UserResponse.builder()
	            .userId(savedUser.getUserId())
	            .name(savedUser.getName())
	            .email(savedUser.getEmail())
	            .role(savedUser.getRole().name())
	            .build();
	}
	// ✅ LOGIN BLOCKED UNTIL VERIFIED
	public LoginResponse login(LoginRequest request) {

	    log.info("Login attempt started");

	    // ✅ Validate input
	    if (request.getEmail() == null || request.getEmail().isBlank()) {
	        log.warn("Login failed: Email is missing");
	        throw new BusinessException("Email is required");
	    }

	    if (request.getPassword() == null || request.getPassword().isBlank()) {
	        log.warn("Login failed: Password is missing for email {}", request.getEmail());
	        throw new BusinessException("Password is required");
	    }

	    log.info("Attempting login for email: {}", request.getEmail());

	    User user = userRepository.findByEmail(request.getEmail())
	            .orElseThrow(() -> {
	                log.warn("Login failed: User not found for email {}", request.getEmail());
	                return new BusinessException("User not found");
	            });

	    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	        log.warn("Login failed: Invalid password for email {}", request.getEmail());
	        throw new BusinessException("Invalid credentials");
	    }

	    if (!user.isVerified()) {
	        log.warn("Login failed: Unverified account for email {}", request.getEmail());
	        throw new BusinessException("Please verify your OTP before logging in");
	    }

	    log.info("Login successful for userId {}", user.getUserId());

	    String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

	    return new LoginResponse(
	            token,
	            user.getRole().name(),
	            user.getUserId()
	    );
	}

	// ✅ VERIFY OTP
	public void verifyOtp(VerifyOtpRequest request) {

	    User user = userRepository.findByEmail(request.getEmail())
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    if (user.isVerified()) {
	        throw new BusinessException("User already verified");
	    }

	    if (user.getOtp() == null) {
	        throw new BusinessException("OTP already used or expired");
	    }

	    if (user.getOtpExpiresAt() == null || user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
	        user.setOtp(null);
	        user.setOtpExpiresAt(null);
	        userRepository.save(user);
	        throw new BusinessException("OTP expired. Please register again or request a new OTP.");
	    }

	    if (!user.getOtp().equals(request.getOtp())) {
	        throw new BusinessException("Invalid OTP");
	    }

	    user.setVerified(true);
	    user.setOtp(null);
	    user.setOtpExpiresAt(null);

	    userRepository.save(user);
	}


	// ✅ GENERATE OTP
	private String generateOtp() {
		return String.valueOf(OTP_RANDOM.nextInt(900000) + 100000);
	}
}
