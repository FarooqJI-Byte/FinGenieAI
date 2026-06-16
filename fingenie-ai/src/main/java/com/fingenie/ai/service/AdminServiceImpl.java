package com.fingenie.ai.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fingenie.ai.dto.AdminDashboardResponse;
import com.fingenie.ai.dto.AdminUserResponse;
import com.fingenie.ai.dto.LoanStatsResponse;
import com.fingenie.ai.repository.AccountRepository;
import com.fingenie.ai.repository.UserRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate;

    @Value("${app.loan-service.base-url}")
    private String loanServiceBaseUrl;

    @Override
    public AdminDashboardResponse getDashboardStats() {

        long users = userRepository.count();
        long accounts = accountRepository.count();

        // ✅ call loan microservice
        LoanStatsResponse loanStats =
                restTemplate.getForObject(
                        loanServiceBaseUrl + "/stats",
                        LoanStatsResponse.class
                );

        return new AdminDashboardResponse(
                users,
                accounts,
                loanStats.getTotalLoans(),
                loanStats.getPendingLoans()
        );
    }
    @Override
    public List<AdminUserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> AdminUserResponse.builder()
                        .userId(user.getUserId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .balance(user.getBalance())
                        .verified(user.isVerified())
                        .build()
                )
                .toList();
    }
}
