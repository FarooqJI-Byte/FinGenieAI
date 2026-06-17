package com.fingenie.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InvestmentResponse {

    private String strategyName;
    private String recommendation;
    private Double expectedReturn;
}