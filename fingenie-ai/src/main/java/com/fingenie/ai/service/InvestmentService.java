package com.fingenie.ai.service;

import com.fingenie.ai.dto.InvestmentRequest;
import com.fingenie.ai.dto.InvestmentResponse;

public interface InvestmentService {

    InvestmentResponse getRecommendation(InvestmentRequest request);
}