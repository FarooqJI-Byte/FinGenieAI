package com.fingenie.ai.dto;

import com.fingenie.ai.enums.AccountType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponse {
		
    private Long accountId;
    private String accountNumber;

    private String bankName;
    private String ifscCode;

    private AccountType accountType;
    private Double balance;
}
