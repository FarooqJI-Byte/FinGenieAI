package com.fingenie.ai.service;

import java.util.List;

import com.fingenie.ai.dto.AdminDashboardResponse;
import com.fingenie.ai.dto.AdminUserResponse;

public interface AdminService {

    AdminDashboardResponse getDashboardStats();
    List<AdminUserResponse> getAllUsers();
}
