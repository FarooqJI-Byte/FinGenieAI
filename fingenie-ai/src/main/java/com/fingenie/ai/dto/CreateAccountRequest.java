package com.fingenie.ai.dto;

import com.fingenie.ai.enums.AccountType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountRequest {

    private Long userId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;
}
