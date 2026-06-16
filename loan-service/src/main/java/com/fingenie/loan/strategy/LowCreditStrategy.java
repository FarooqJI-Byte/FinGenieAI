package com.fingenie.loan.strategy;

import org.springframework.stereotype.Component;

import com.fingenie.loan.enums.LoanStatus;

@Component
public class LowCreditStrategy implements LoanApprovalStrategy {

    @Override
    public LoanStatus approve(double income, double creditScore, double amount) {
        return LoanStatus.REJECTED;
    }
}
