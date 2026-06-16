package com.fingenie.ai.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class InvestmentResponse {

    private String strategyName;
    private String recommendation;
    private Double expectedReturn;
}