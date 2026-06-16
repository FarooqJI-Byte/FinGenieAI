package com.fingenie.ai.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.client.RestTemplate;

import com.fingenie.ai.dto.AdminDashboardResponse;
import com.fingenie.ai.dto.AdminUserResponse;
import com.fingenie.ai.dto.LoanStatsResponse;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.enums.Role;
import com.fingenie.ai.repository.AccountRepository;
import com.fingenie.ai.repository.UserRepository;

/**
 * ✅ Unit Test for AdminServiceImpl
 *
 * Covers:
 * - Dashboard stats (with external microservice call)
 * - User list mapping
 * - Mocking RestTemplate (VERY IMPORTANT)
 */
class AdminServiceImplTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * ✅ TEST: getDashboardStats SUCCESS
     *
     * Tests:
     * - DB counts (users, accounts)
     * - External loan microservice call
     * - Correct aggregation into response
     */
    @Test
    void getDashboardStats_success() {

        // Mock DB counts
        when(userRepository.count()).thenReturn(10L);
        when(accountRepository.count()).thenReturn(5L);

        // Mock external Loan microservice response
        LoanStatsResponse loanStats = new LoanStatsResponse(20L, 7L);

        when(restTemplate.getForObject(
                "http://localhost:8081/loans/stats",
                LoanStatsResponse.class))
                .thenReturn(loanStats);

        // Call service
        AdminDashboardResponse response = adminService.getDashboardStats();

        // Assertions
        assertNotNull(response);
        assertEquals(10L, response.getTotalUsers());
        assertEquals(5L, response.getTotalAccounts());
        assertEquals(20L, response.getTotalLoans());
        assertEquals(7L, response.getPendingLoans());
    }

    /**
     * ❌ TEST: getDashboardStats FAIL - loan service returns null
     *
     * Edge case: external service returns null
     */
    @Test
    void getDashboardStats_shouldThrow_whenLoanServiceReturnsNull() {

        when(userRepository.count()).thenReturn(10L);
        when(accountRepository.count()).thenReturn(5L);

        // External API returns null
        when(restTemplate.getForObject(
                "http://localhost:8081/loans/stats",
                LoanStatsResponse.class))
                .thenReturn(null);

        // Since your current code does NOT handle null,
        // this will throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> adminService.getDashboardStats());
    }

    /**
     * ✅ TEST: getAllUsers SUCCESS
     *
     * Tests mapping logic from User -> AdminUserResponse
     */
    @Test
    void getAllUsers_success() {

        User user = User.builder()
                .userId(1L)
                .name("John")
                .email("john@gmail.com")
                .role(Role.CUSTOMER)
                .balance(5000.0)
                .verified(true)
                .build();

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<AdminUserResponse> result = adminService.getAllUsers();

        // Assertions
        assertEquals(1, result.size());

        AdminUserResponse response = result.get(0);

        assertEquals(1L, response.getUserId());
        assertEquals("John", response.getName());
        assertEquals("john@gmail.com", response.getEmail());
        assertEquals(Role.CUSTOMER, response.getRole());
        assertEquals(5000.0, response.getBalance());
        assertTrue(response.isVerified());
    }

    /**
     * ✅ TEST: getAllUsers SUCCESS - empty list
     *
     * Edge case: no users in DB
     */
    @Test
    void getAllUsers_emptyList() {

        when(userRepository.findAll())
                .thenReturn(List.of());

        List<AdminUserResponse> result = adminService.getAllUsers();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * ✅ BONUS TEST: verify RestTemplate is called once
     */
    @Test
    void getDashboardStats_verifyRestCall() {

        when(userRepository.count()).thenReturn(1L);
        when(accountRepository.count()).thenReturn(1L);

        when(restTemplate.getForObject(anyString(), eq(LoanStatsResponse.class)))
                .thenReturn(new LoanStatsResponse(1L, 0L));

        adminService.getDashboardStats();

        // Verify external API call happens exactly once
        verify(restTemplate, times(1))
                .getForObject("http://localhost:8081/loans/stats",
                        LoanStatsResponse.class);
    }
}
