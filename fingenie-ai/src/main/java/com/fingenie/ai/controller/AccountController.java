package com.fingenie.ai.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fingenie.ai.dto.AccountResponse;
import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.dto.CreateAccountRequest;
import com.fingenie.ai.service.AccountServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl accountService;

    // Create account - customer only
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        AccountResponse account = accountService.createAccount(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Account created successfully",
                        account,
                        LocalDateTime.now()
                )
        );
    }

    // Get logged-in user's accounts
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts() {

        List<AccountResponse> accounts = accountService.getMyAccounts();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Accounts fetched successfully",
                        accounts,
                        LocalDateTime.now()
                )
        );
    }

    // Get account by account id
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @PathVariable Long accountId) {

        AccountResponse account = accountService.getAccountById(accountId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Account fetched successfully",
                        account,
                        LocalDateTime.now()
                )
        );
    }

    // Check balance
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<ApiResponse<Double>> getBalance(
            @PathVariable Long accountId) {

        Double balance = accountService.getBalance(accountId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Balance fetched successfully",
                        balance,
                        LocalDateTime.now()
                )
        );
    }
 // Admin only - view all accounts
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts() {

        List<AccountResponse> accounts = accountService.getAllAccounts();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "All accounts fetched successfully",
                        accounts,
                        LocalDateTime.now()
                )
        );
    }

    // Admin only - view accounts of any user
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getUserAccounts(
            @PathVariable Long userId) {

        List<AccountResponse> accounts = accountService.getAccountsByUser(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "User accounts fetched successfully",
                        accounts,
                        LocalDateTime.now()
                )
        );
    }
 
}
