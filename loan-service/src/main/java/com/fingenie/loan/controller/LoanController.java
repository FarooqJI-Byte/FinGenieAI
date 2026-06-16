package com.fingenie.loan.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fingenie.loan.dto.ApiResponse;
import com.fingenie.loan.dto.LoanRequest;
import com.fingenie.loan.dto.LoanStatsResponse;
import com.fingenie.loan.entity.Loan;
import com.fingenie.loan.service.LoanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/loans")
@CrossOrigin(origins = "${app.cors.allowed-origin}")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    // Apply loan
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<String>> applyLoan(
            @Valid @RequestBody LoanRequest request) {

        String result = loanService.applyLoan(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Loan processed successfully",
                        result,
                        LocalDateTime.now()
                )
        );
    }

    // Get all loans
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Loan>>> getAllLoans() {

        List<Loan> loans = loanService.getAllLoans();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "All loans fetched",
                        loans,
                        LocalDateTime.now()
                )
        );
    }

    // Get loans by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Loan>>> getLoansByUser(
            @PathVariable Long userId) {

        List<Loan> loans = loanService.getLoansByUser(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "User loans fetched successfully",
                        loans,
                        LocalDateTime.now()
                )
        );
    }

    // Loan stats for admin dashboard
    @GetMapping("/stats")
    public LoanStatsResponse getLoanStats() {
        return loanService.getLoanStats();
    }

    // Approve loan
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<String>> approveLoan(
            @PathVariable Long id) {

        String result = loanService.approveLoan(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Loan approved successfully",
                        result,
                        LocalDateTime.now()
                )
        );
    }

    // Reject loan
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<String>> rejectLoan(
            @PathVariable Long id) {

        String result = loanService.rejectLoan(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Loan rejected successfully",
                        result,
                        LocalDateTime.now()
                )
        );
    }
}
