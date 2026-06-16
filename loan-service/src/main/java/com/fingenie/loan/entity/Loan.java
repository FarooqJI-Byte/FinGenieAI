package com.fingenie.loan.entity;

import com.fingenie.loan.enums.LoanStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    private Double amount;

    private Double income;

    private Double creditScore;

    private Long userId; // ✅ instead of User entity

    @Enumerated(EnumType.STRING)
    private LoanStatus status;
}