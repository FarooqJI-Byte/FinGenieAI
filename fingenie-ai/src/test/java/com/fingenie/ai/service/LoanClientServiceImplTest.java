package com.fingenie.ai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.dto.LoanRequest;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoanClientServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanClientServiceImpl loanClientService;

    private User mockUser;

    private static final String BASE_URL = "http://localhost:8081/loans";

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .userId(1L)
                .email("test@gmail.com")
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("test@gmail.com", null, null);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private ApiResponse<Object> buildResponse(String message) {
        return new ApiResponse<>(200, message, null, LocalDateTime.now());
    }

    @Test
    void testApplyLoan() {
        LoanRequest request = new LoanRequest();
        ApiResponse<Object> mockResponse = buildResponse("Success");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(mockUser));

        when(restTemplate.postForObject(
                BASE_URL + "/apply",
                request,
                ApiResponse.class))
                .thenReturn(mockResponse);

        ApiResponse<?> response = loanClientService.applyLoan(request);

        assertNotNull(response);
        assertEquals("Success", response.getMessage());

        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        verify(restTemplate, times(1))
                .postForObject(BASE_URL + "/apply", request, ApiResponse.class);
    }

    @Test
    void testGetMyLoans() {
        ApiResponse<Object> mockResponse = buildResponse("Fetched");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(mockUser));

        when(restTemplate.getForObject(
                BASE_URL + "/user/1",
                ApiResponse.class))
                .thenReturn(mockResponse);

        ApiResponse<?> response = loanClientService.getMyLoans();

        assertNotNull(response);
        assertEquals("Fetched", response.getMessage());
    }

    @Test
    void testGetLoansByUser() {
        ApiResponse<Object> mockResponse = buildResponse("User Loans");

        when(restTemplate.getForObject(
                BASE_URL + "/user/1",
                ApiResponse.class))
                .thenReturn(mockResponse);

        ApiResponse<?> response = loanClientService.getLoansByUser(1L);

        assertNotNull(response);
        assertEquals("User Loans", response.getMessage());
    }

    @Test
    void testGetAllLoans() {
        ApiResponse<Object> mockResponse = buildResponse("All Loans");

        when(restTemplate.getForObject(
                BASE_URL + "/all",
                ApiResponse.class))
                .thenReturn(mockResponse);

        ApiResponse<?> response = loanClientService.getAllLoans();

        assertNotNull(response);
        assertEquals("All Loans", response.getMessage());
    }

    @Test
    void testApproveLoan() {
        ApiResponse<Object> mockResponse = buildResponse("Approved");

        when(restTemplate.exchange(
                BASE_URL + "/1/approve",
                HttpMethod.PUT,
                null,
                ApiResponse.class))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ApiResponse<?> response = loanClientService.approveLoan(1L);

        assertNotNull(response);
        assertEquals("Approved", response.getMessage());
    }

    @Test
    void testRejectLoan() {
        ApiResponse<Object> mockResponse = buildResponse("Rejected");

        when(restTemplate.exchange(
                BASE_URL + "/1/reject",
                HttpMethod.PUT,
                null,
                ApiResponse.class))
                .thenReturn(ResponseEntity.ok(mockResponse));

        ApiResponse<?> response = loanClientService.rejectLoan(1L);

        assertNotNull(response);
        assertEquals("Rejected", response.getMessage());
    }
}
