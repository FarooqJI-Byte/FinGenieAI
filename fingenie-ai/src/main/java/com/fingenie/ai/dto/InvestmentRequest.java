package com.fingenie.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InvestmentRequest {

    @NotNull(message = "Monthly income is required")
    @Positive(message = "Monthly income must be greater than zero")
    private Double monthlyIncome;

    @NotBlank(message = "Risk level is required")
    @Pattern(regexp = "LOW|MEDIUM|HIGH", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Risk level must be LOW, MEDIUM, or HIGH")
    private String riskLevel; // LOW, MEDIUM, HIGH
}
