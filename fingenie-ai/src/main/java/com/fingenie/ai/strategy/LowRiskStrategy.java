package com.fingenie.ai.strategy;

import org.springframework.stereotype.Component;

import com.fingenie.ai.dto.InvestmentRequest;
import com.fingenie.ai.dto.InvestmentResponse;

@Component("LOW")
public class LowRiskStrategy implements InvestmentStrategy {

    @Override
    public InvestmentResponse suggest(InvestmentRequest request) {

        return InvestmentResponse.builder()
                .strategyName("Low Risk Plan")
                .recommendation("70% Fixed Deposits, 20% Bonds, 10% Mutual Funds")
                .expectedReturn(6.5)
                .build();
    }
}
