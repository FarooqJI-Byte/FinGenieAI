package com.fingenie.ai.strategy;

import com.fingenie.ai.dto.InvestmentRequest;
import com.fingenie.ai.dto.InvestmentResponse;

public interface InvestmentStrategy {

    InvestmentResponse suggest(InvestmentRequest request);
}
