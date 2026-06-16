package com.fingenie.ai.dto;

import com.fingenie.ai.enums.TransactionStatus;
import com.fingenie.ai.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse {

    private Long transactionId;
    private Double amount;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime date;
    private Boolean fraudFlag;
    private Double riskScore;

}