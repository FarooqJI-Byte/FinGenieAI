package com.fingenie.loan.strategy;

import org.springframework.stereotype.Component;

import com.fingenie.loan.enums.LoanStatus;
@Component
public class MediumCreditStrategy implements LoanApprovalStrategy {

    @Override
    public LoanStatus approve(double income, double creditScore, double amount) {

        if (income >= amount * 0.5) {
            return LoanStatus.PENDING; // needs admin check
        }

        return LoanStatus.REJECTED;
    }
}
