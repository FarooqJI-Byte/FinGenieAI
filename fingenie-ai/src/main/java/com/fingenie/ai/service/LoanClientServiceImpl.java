package com.fingenie.ai.service;

import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.dto.LoanRequest;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.exception.ResourceNotFoundException;
import com.fingenie.ai.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanClientServiceImpl implements LoanClientService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Value("${app.loan-service.base-url}")
    private String loanServiceBaseUrl;

    private User getLoggedInUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public ApiResponse<?> applyLoan(LoanRequest request) {

        User user = getLoggedInUser();

        // Important: frontend should not send userId
        request.setUserId(user.getUserId());

        String url = loanServiceBaseUrl + "/apply";

        return restTemplate.postForObject(
                url,
                request,
                ApiResponse.class
        );
    }

    @Override
    public ApiResponse<?> getLoansByUser(Long userId) {

        String url = loanServiceBaseUrl + "/user/" + userId;

        return restTemplate.getForObject(
                url,
                ApiResponse.class
        );
    }

    @Override
    public ApiResponse<?> getAllLoans() {

        String url = loanServiceBaseUrl + "/all";

        return restTemplate.getForObject(
                url,
                ApiResponse.class
        );
    }

    @Override
    public ApiResponse<?> getMyLoans() {

        User user = getLoggedInUser();

        return getLoansByUser(user.getUserId());
    }

    @Override
    public ApiResponse<?> approveLoan(Long loanId) {

        String url = loanServiceBaseUrl + "/" + loanId + "/approve";

        return restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                ApiResponse.class
        ).getBody();
    }

    @Override
    public ApiResponse<?> rejectLoan(Long loanId) {

        String url = loanServiceBaseUrl + "/" + loanId + "/reject";

        return restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                ApiResponse.class
        ).getBody();
    }
}
