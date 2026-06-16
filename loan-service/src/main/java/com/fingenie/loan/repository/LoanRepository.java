package com.fingenie.loan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fingenie.loan.entity.Loan;
import com.fingenie.loan.enums.LoanStatus;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserId(Long userId);

    long countByStatus(LoanStatus status);
}