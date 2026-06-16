package com.fingenie.ai.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fingenie.ai.dto.AdminDashboardResponse;
import com.fingenie.ai.dto.AdminUserResponse;
import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class AdminController {

    private final AdminService adminService;

    // ✅ ADMIN DASHBOARD API
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {

        AdminDashboardResponse data = adminService.getDashboardStats();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Admin dashboard fetched successfully",
                        data,
                        LocalDateTime.now()
                )
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers() {

        List<AdminUserResponse> users = adminService.getAllUsers();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Users fetched successfully",
                        users,
                        LocalDateTime.now()
                )
        );
    }
  
}
