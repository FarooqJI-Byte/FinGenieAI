package com.fingenie.loan.service;

import java.util.List;

import com.fingenie.loan.dto.LoanRequest;
import com.fingenie.loan.dto.LoanStatsResponse;
import com.fingenie.loan.entity.Loan;

public interface LoanService {

    String applyLoan(LoanRequest request);

    List<Loan> getAllLoans();

    List<Loan> getLoansByUser(Long userId);

    LoanStatsResponse getLoanStats();

    String approveLoan(Long id);

    String rejectLoan(Long id);
}