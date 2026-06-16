package com.fingenie.ai.service;

import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.dto.LoanRequest;


public interface LoanClientService {

    ApiResponse<?> applyLoan(LoanRequest request);

    ApiResponse<?> getLoansByUser(Long userId);

    ApiResponse<?> getAllLoans();

    ApiResponse<?> getMyLoans();

    ApiResponse<?> approveLoan(Long loanId);

    ApiResponse<?> rejectLoan(Long loanId);
}

