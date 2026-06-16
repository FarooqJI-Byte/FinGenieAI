package com.fingenie.ai.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fingenie.ai.dto.CreateAccountRequest;
import com.fingenie.ai.entity.Account;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.enums.AccountType;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.exception.ResourceNotFoundException;
import com.fingenie.ai.repository.AccountRepository;
import com.fingenie.ai.repository.UserRepository;

/**
 * ✅ Unit Test Class for AccountServiceImpl
 * 
 * Covers:
 * - Positive scenarios (success)
 * - Negative scenarios (exceptions)
 * - Business validations
 * - SecurityContext mocking (JWT user)
 */
class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    /**
     * ✅ Setup runs before each test
     * Mocks SecurityContext (JWT authentication)
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@gmail.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * ✅ TEST: createAccount SUCCESS
     */
    @Test
    void createAccount_success() {

        // Prepare request
        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.SAVINGS);

        // Mock user
        User user = User.builder()
                .userId(1L)
                .email("test@gmail.com")
                .build();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        // User has less than max accounts
        when(accountRepository.countByUserAndAccountType(user, AccountType.SAVINGS))
                .thenReturn(1L);

        // Account number not exists
        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());

        // Save returns same object
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = accountService.createAccount(request);

        // Assertions
        assertNotNull(response);
        assertEquals("FinGenie Bank", response.getBankName());
        assertEquals("FGNB0001234", response.getIfscCode());

        // Verify save called
        verify(accountRepository).save(any(Account.class));
    }

    /**
     * ❌ TEST: createAccount FAIL - max accounts reached
     */
    @Test
    void createAccount_shouldThrow_whenLimitExceeded() {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.SAVINGS);

        User user = User.builder()
                .userId(1L)
                .email("test@gmail.com")
                .build();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        // Limit reached
        when(accountRepository.countByUserAndAccountType(user, AccountType.SAVINGS))
                .thenReturn(3L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.createAccount(request));

        assertEquals("Maximum 3 accounts of this type allowed", ex.getMessage());
    }

    /**
     * ❌ TEST: createAccount FAIL - user not found
     */
    @Test
    void createAccount_shouldThrow_whenUserNotFound() {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.SAVINGS);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> accountService.createAccount(request));
    }

    /**
     * ❌ TEST: getAccountById FAIL - null input
     */
    @Test
    void getAccountById_shouldThrow_whenIdIsNull() {

        assertThrows(BusinessException.class,
                () -> accountService.getAccountById(null));
    }

    /**
     * ❌ TEST: getAccountById FAIL - not found
     */
    @Test
    void getAccountById_shouldThrow_whenNotFound() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> accountService.getAccountById(1L));
    }

    /**
     * ✅ TEST: getAccountById SUCCESS
     */
    @Test
    void getAccountById_success() {

        Account account = Account.builder()
                .accountId(1L)
                .accountNumber("123456789012")
                .balance(1000.0)
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        var result = accountService.getAccountById(1L);

        assertEquals(1L, result.getAccountId());
    }

    /**
     * ✅ TEST: getAccountsByUser SUCCESS
     */
    @Test
    void getAccountsByUser_success() {

        User user = User.builder().userId(1L).build();

        Account account = Account.builder()
                .accountId(101L)
                .accountNumber("123456789012")
                .user(user)
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(accountRepository.findByUser(user))
                .thenReturn(List.of(account));

        var result = accountService.getAccountsByUser(1L);

        assertEquals(1, result.size());
    }

    /**
     * ❌ TEST: getAccountsByUser FAIL - no accounts
     */
    @Test
    void getAccountsByUser_shouldThrow_whenEmpty() {

        User user = User.builder().userId(1L).build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(accountRepository.findByUser(user))
                .thenReturn(List.of());

        assertThrows(BusinessException.class,
                () -> accountService.getAccountsByUser(1L));
    }

    /**
     * ✅ TEST: getBalance SUCCESS
     */
    @Test
    void getBalance_success() {

        Account account = Account.builder()
                .accountId(1L)
                .balance(5000.0)
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        Double balance = accountService.getBalance(1L);

        assertEquals(5000.0, balance);
    }

    /**
     * ❌ TEST: getBalance FAIL - null id
     */
    @Test
    void getBalance_shouldThrow_whenNullId() {

        assertThrows(BusinessException.class,
                () -> accountService.getBalance(null));
    }

    /**
     * ✅ TEST: getMyAccounts SUCCESS (JWT user)
     */
    @Test
    void getMyAccounts_success() {

        Account account = Account.builder()
                .accountId(1L)
                .accountNumber("111222333")
                .build();

        when(accountRepository.findByUserEmail("test@gmail.com"))
                .thenReturn(List.of(account));

        var result = accountService.getMyAccounts();

        assertEquals(1, result.size());
    }

    /**
     * ✅ TEST: getAllAccounts SUCCESS (Admin)
     */
    @Test
    void getAllAccounts_success() {

        Account account = Account.builder()
                .accountId(1L)
                .build();

        when(accountRepository.findAll())
                .thenReturn(List.of(account));

        var result = accountService.getAllAccounts();

        assertEquals(1, result.size());
    }
}
