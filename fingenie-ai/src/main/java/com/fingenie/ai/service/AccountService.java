package com.fingenie.ai.service;

import java.util.List;

import com.fingenie.ai.dto.AccountResponse;
import com.fingenie.ai.dto.CreateAccountRequest;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);
    AccountResponse getAccountById(Long accountId);
    List<AccountResponse> getAccountsByUser(Long userId);
    Double getBalance(Long accountId);
    public List<AccountResponse> getMyAccounts();
    List<AccountResponse> getAllAccounts();

}
