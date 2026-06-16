package com.fingenie.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResultResponse {

    private Double amount;
    private String message;
    private Double currentBalance;
}