package com.fingenie.ai.service;

import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.dto.LoanRequest;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.exception.ResourceNotFoundException;
import com.fingenie.ai.repository.UserRepository;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanClientServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanClientServiceImpl loanService;

    private User user;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(
                loanService,
                "loanServiceBaseUrl",
                "http://localhost:8082"
        );

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@gmail.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@gmail.com", null)
        );
    }

    // ✅ APPLY LOAN
    @Test
    void applyLoan_shouldCallPostAPI() {

        LoanRequest request = new LoanRequest();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        ApiResponse<Object> mockResponse = new ApiResponse<>(
                200,
                "success",
                null,
                LocalDateTime.now()
        );

        when(restTemplate.postForObject(
                "http://localhost:8082/apply",
                request,
                ApiResponse.class
        )).thenReturn(mockResponse);

        ApiResponse<?> response = loanService.applyLoan(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());

        verify(restTemplate).postForObject(
                eq("http://localhost:8082/apply"),
                eq(request),
                eq(ApiResponse.class)
        );

        assertEquals(1L, request.getUserId());
    }

    // ✅ APPLY LOAN - USER NOT FOUND
    @Test
    void applyLoan_shouldThrow_ifUserNotFound() {

        LoanRequest request = new LoanRequest();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loanService.applyLoan(request));
    }

    // ✅ GET LOANS BY USER
    @Test
    void getLoansByUser_shouldCallGetAPI() {

        ApiResponse<Object> mockResponse = new ApiResponse<>(
                200,
                "ok",
                null,
                LocalDateTime.now()
        );

        when(restTemplate.getForObject(
                "http://localhost:8082/user/1",
                ApiResponse.class
        )).thenReturn(mockResponse);

        ApiResponse<?> response = loanService.getLoansByUser(1L);

        assertNotNull(response);
        verify(restTemplate).getForObject(
                "http://localhost:8082/user/1",
                ApiResponse.class
        );
    }

    // ✅ GET ALL LOANS
    @Test
    void getAllLoans_shouldCallGetAPI() {

        ApiResponse<Object> mockResponse = new ApiResponse<>(
                200,
                "ok",
                null,
                LocalDateTime.now()
        );

        when(restTemplate.getForObject(
                "http://localhost:8082/all",
                ApiResponse.class
        )).thenReturn(mockResponse);

        ApiResponse<?> response = loanService.getAllLoans();

        assertNotNull(response);
    }

    // ✅ GET MY LOANS
    @Test
    void getMyLoans_shouldCallUserLoans() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        ApiResponse<Object> mockResponse = new ApiResponse<>(
                200,
                "ok",
                null,
                LocalDateTime.now()
        );

        when(restTemplate.getForObject(
                "http://localhost:8082/user/1",
                ApiResponse.class
        )).thenReturn(mockResponse);

        ApiResponse<?> response = loanService.getMyLoans();

        assertNotNull(response);
    }

    // ✅ APPROVE LOAN
    @Test
    void approveLoan_shouldCallPutAPI() {

        ApiResponse<Object> mockResponse = new ApiResponse<>(
                200,
                "approved",
                null,
                LocalDateTime.now()
        );

        when(restTemplate.exchange(
                eq("http://localhost:8082/10/approve"),
                eq(HttpMethod.PUT),
                isNull(),
                eq(ApiResponse.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        ApiResponse<?> response = loanService.approveLoan(10L);

        assertNotNull(response);
        assertEquals("approved", response.getMessage());
        assertTrue(response.isSuccess());
    }

    // ✅ REJECT LOAN
    @Test
    void rejectLoan_shouldCallPutAPI() {

        ApiResponse<Object> mockResponse = new ApiResponse<>(
                200,
                "rejected",
                null,
                LocalDateTime.now()
        );

        when(restTemplate.exchange(
                eq("http://localhost:8082/10/reject"),
                eq(HttpMethod.PUT),
                isNull(),
                eq(ApiResponse.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        ApiResponse<?> response = loanService.rejectLoan(10L);

        assertNotNull(response);
        assertEquals("rejected", response.getMessage());
        assertTrue(response.isSuccess());
    }
}