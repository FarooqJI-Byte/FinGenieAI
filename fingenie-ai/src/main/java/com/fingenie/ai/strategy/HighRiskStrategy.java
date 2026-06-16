package com.fingenie.ai.strategy;

import org.springframework.stereotype.Component;

import com.fingenie.ai.dto.InvestmentRequest;
import com.fingenie.ai.dto.InvestmentResponse;

@Component("HIGH")
public class HighRiskStrategy implements InvestmentStrategy {

    @Override
    public InvestmentResponse suggest(InvestmentRequest request) {

        return InvestmentResponse.builder()
                .strategyName("Aggressive Growth Plan")
                .recommendation("70% Stocks, 20% Crypto, 10% Mutual Funds")
                .expectedReturn(18.0)
                .build();
    }
}