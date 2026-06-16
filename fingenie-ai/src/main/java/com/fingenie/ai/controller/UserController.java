package com.fingenie.ai.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	private final UserRepository userRepository;
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/id")
	public ResponseEntity<ApiResponse<Long>> getMyUserId() {

	    String email = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    return ResponseEntity.ok(
	            new ApiResponse<>(
	                    200,
	                    "User ID fetched successfully",
	                    user.getUserId(),
	                    LocalDateTime.now()
	            )
	    );
	}

}
