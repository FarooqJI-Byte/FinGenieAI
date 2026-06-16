package com.fingenie.ai.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.dto.DepositRequest;
import com.fingenie.ai.dto.TransactionResponse;
import com.fingenie.ai.dto.TransactionResultResponse;
import com.fingenie.ai.dto.TransferRequest;
import com.fingenie.ai.dto.WithdrawRequest;
import com.fingenie.ai.enums.TransactionType;
import com.fingenie.ai.service.TransactionServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class TransactionController {

    private final TransactionServiceImpl transactionService;
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResultResponse>> deposit(
            @Valid @RequestBody DepositRequest request) {

        TransactionResultResponse result = transactionService.deposit(request);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Deposit successful", result,LocalDateTime.now())
        );
    }
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResultResponse>> withdraw(
            @Valid @RequestBody WithdrawRequest request) {

        TransactionResultResponse result = transactionService.withdraw(request);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Withdraw successful", result,LocalDateTime.now())
        );
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResultResponse>> transfer(
            @Valid @RequestBody TransferRequest request) {

        TransactionResultResponse result = transactionService.transfer(request);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Transfer successful", result,LocalDateTime.now())
        );
    }
    
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @PathVariable Long accountId) {

        List<TransactionResponse> transactions =
                transactionService.getTransactionHistory(accountId);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Transactions fetched successfully", transactions,LocalDateTime.now())
        );
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{accountId}/transactions/type")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByType(
            @PathVariable Long accountId,
            @RequestParam TransactionType type) {

        List<TransactionResponse> transactions =
                transactionService.getTransactionsByType(accountId, type);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Filtered transactions fetched", transactions,LocalDateTime.now())
        );
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{accountId}/transactions/date")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByDate(
            @PathVariable Long accountId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<TransactionResponse> transactions =
                transactionService.getTransactionsByDate(accountId, startDate, endDate);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Transactions fetched by date", transactions,LocalDateTime.now())
        );
    }
}
