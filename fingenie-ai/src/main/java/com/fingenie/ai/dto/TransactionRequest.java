package com.fingenie.ai.dto;

import lombok.*;

@Getter
@Setter
public class TransactionRequest {

    private Double amount;
    private String type; // DEPOSIT / WITHDRAW / TRANSFER
}