package com.fingenie.loan.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fingenie.loan.dto.LoanRequest;
import com.fingenie.loan.dto.LoanStatsResponse;
import com.fingenie.loan.entity.Loan;
import com.fingenie.loan.enums.LoanStatus;
import com.fingenie.loan.repository.LoanRepository;
import com.fingenie.loan.strategy.LoanApprovalStrategy;
import com.fingenie.loan.strategy.LoanStrategyFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final LoanStrategyFactory strategyFactory;

    @Override
    public String applyLoan(LoanRequest request) {

        log.info("Loan request received for userId: {}", request.getUserId());

        LoanApprovalStrategy strategy =
                strategyFactory.getStrategy(request.getCreditScore());

        // ✅ Get final status from strategy
        LoanStatus status = strategy.approve(
                request.getIncome(),
                request.getCreditScore(),
                request.getAmount()
        );

        // ✅ Build loan entity
        Loan loan = Loan.builder()
                .amount(request.getAmount())
                .income(request.getIncome())
                .creditScore(request.getCreditScore())
                .userId(request.getUserId())
                .status(status)
                .build();

        // ✅ Save to DB FIRST
        loanRepository.save(loan);

        log.info("Loan request saved for userId {} with status {}", 
                request.getUserId(), status);

        // ✅ THEN return response
        if (status == LoanStatus.APPROVED) {
            return "Loan approved automatically";
        }
        else if (status == LoanStatus.PENDING) {
            return "Loan application submitted for admin review";
        }
        else {
            return "Loan rejected based on eligibility rules";
        }
    }
    @Override
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Override
    public List<Loan> getLoansByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    @Override
    public LoanStatsResponse getLoanStats() {

        long total = loanRepository.count();
        long pending = loanRepository.countByStatus(LoanStatus.PENDING);

        return new LoanStatsResponse(total, pending);
    }

    @Override
    public String approveLoan(Long id) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setStatus(LoanStatus.APPROVED);
        loanRepository.save(loan);

        log.info("Loan {} approved", id);

        return "Loan approved";
    }

    @Override
    public String rejectLoan(Long id) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setStatus(LoanStatus.REJECTED);
        loanRepository.save(loan);

        log.info("Loan {} rejected", id);

        return "Loan rejected";
    }
}