package com.fingenie.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoanStatsResponse {

    private long totalLoans;
    private long pendingLoans;
}
