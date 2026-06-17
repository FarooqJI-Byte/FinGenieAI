package com.fingenie.ai.service;

import com.fingenie.ai.dto.AdminDashboardResponse;
import com.fingenie.ai.dto.AdminUserResponse;
import com.fingenie.ai.dto.LoanStatsResponse;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.enums.Role;
import com.fingenie.ai.repository.AccountRepository;
import com.fingenie.ai.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setup() {
        // ✅ manually inject @Value field
        ReflectionTestUtils.setField(
                adminService,
                "loanServiceBaseUrl",
                "http://localhost:8082"
        );
    }

    // ✅ Test Dashboard Stats
    @Test
    void getDashboardStats_shouldReturnCorrectData() {

        // mocking DB counts
        when(userRepository.count()).thenReturn(10L);
        when(accountRepository.count()).thenReturn(25L);

        // mocking microservice response
        LoanStatsResponse loanStats = new LoanStatsResponse(50L, 5L);

        when(restTemplate.getForObject(
                "http://localhost:8082/stats",
                LoanStatsResponse.class
        )).thenReturn(loanStats);

        // call service
        AdminDashboardResponse response = adminService.getDashboardStats();

        // assertions
        assertNotNull(response);
        assertEquals(10L, response.getTotalUsers());
        assertEquals(25L, response.getTotalAccounts());
        assertEquals(50L, response.getTotalLoans());
        assertEquals(5L, response.getPendingLoans());
    }

    // ✅ Test getAllUsers
    @Test
    void getAllUsers_shouldReturnMappedUsers() {

        User user1 = new User();
        user1.setUserId(1L);
        user1.setName("Farooq");
        user1.setEmail("farooq@gmail.com");
        user1.setRole(Role.CUSTOMER);
        user1.setBalance(1000.0);
        user1.setVerified(true);

        User user2 = new User();
        user2.setUserId(2L);
        user2.setName("Admin");
        user2.setEmail("admin@gmail.com");
        user2.setRole(Role.ADMIN);
        user2.setBalance(0.0);
        user2.setVerified(true);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<AdminUserResponse> result = adminService.getAllUsers();

        assertEquals(2, result.size());

        AdminUserResponse first = result.get(0);
        assertEquals("Farooq", first.getName());
        assertEquals("farooq@gmail.com", first.getEmail());
        assertEquals(Role.CUSTOMER, first.getRole());
    }
}