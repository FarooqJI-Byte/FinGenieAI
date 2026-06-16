package com.fingenie.ai.strategy;

import org.springframework.stereotype.Component;

import com.fingenie.ai.dto.InvestmentRequest;
import com.fingenie.ai.dto.InvestmentResponse;

@Component("MEDIUM")
public class MediumRiskStrategy implements InvestmentStrategy {

    @Override
    public InvestmentResponse suggest(InvestmentRequest request) {

        return InvestmentResponse.builder()
                .strategyName("Balanced Plan")
                .recommendation("40% Mutual Funds, 30% Stocks, 20% Bonds, 10% Fixed Deposits")
                .expectedReturn(10.0)
                .build();
    }
}
