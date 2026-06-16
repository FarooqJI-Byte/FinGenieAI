package com.fingenie.loan.strategy;

import com.fingenie.loan.enums.LoanStatus;

public interface LoanApprovalStrategy {

    LoanStatus approve(double income, double creditScore, double amount);
}