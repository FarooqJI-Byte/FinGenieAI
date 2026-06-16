package com.fingenie.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.dto.LoanRequest;
import com.fingenie.ai.service.LoanClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class LoanController {

    private final LoanClientService loanClientService;

    // Customer applies for loan
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<?>> applyLoan(
            @Valid @RequestBody LoanRequest request) {

        return ResponseEntity.ok(
                loanClientService.applyLoan(request)
        );
    }

    // Customer gets own loans
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<?>> getMyLoans() {

        return ResponseEntity.ok(
                loanClientService.getMyLoans()
        );
    }

    // Admin gets loans of a specific user
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getLoansByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                loanClientService.getLoansByUser(userId)
        );
    }

    // Admin gets all loans
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllLoans() {

        return ResponseEntity.ok(
                loanClientService.getAllLoans()
        );
    }

    // Admin approves loan
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{loanId}/approve")
    public ResponseEntity<ApiResponse<?>> approveLoan(
            @PathVariable Long loanId) {

        return ResponseEntity.ok(
                loanClientService.approveLoan(loanId)
        );
    }

    // Admin rejects loan
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{loanId}/reject")
    public ResponseEntity<ApiResponse<?>> rejectLoan(
            @PathVariable Long loanId) {

        return ResponseEntity.ok(
                loanClientService.rejectLoan(loanId)
        );
    }
}
