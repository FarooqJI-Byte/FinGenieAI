package com.fingenie.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor

public class LoanStatsResponse {

    private long totalLoans;
    private long pendingLoans;
}