package com.fingenie.loan.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanStrategyFactory {

    private final HighCreditStrategy high;
    private final MediumCreditStrategy medium;
    private final LowCreditStrategy low;

    public LoanApprovalStrategy getStrategy(double creditScore) {

        if (creditScore > 750) return high;
        if (creditScore > 600) return medium;
        return low;
    }
}