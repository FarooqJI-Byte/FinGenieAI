package com.fingenie.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalAccounts;
    private long totalLoans;
    private long pendingLoans;
}