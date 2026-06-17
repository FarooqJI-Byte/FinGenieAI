package com.fingenie.ai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fingenie.ai.dto.AccountResponse;
import com.fingenie.ai.dto.CreateAccountRequest;
import com.fingenie.ai.entity.Account;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.enums.AccountType;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.repository.AccountRepository;
import com.fingenie.ai.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@gmail.com");

        // ✅ Mock logged-in user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@gmail.com", null)
        );
    }

    // ✅ createAccount - success
    @Test
    void createAccount_shouldCreateSuccessfully() {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.SAVINGS);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(accountRepository.countByUserAndAccountType(user, AccountType.SAVINGS))
                .thenReturn(1L);

        when(accountRepository.findByAccountNumber(anyString()))
                .thenReturn(Optional.empty());

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    Account acc = invocation.getArgument(0);
                    acc.setAccountId(100L);
                    return acc;
                });

        AccountResponse response = accountService.createAccount(request);

        assertNotNull(response);
        assertEquals(100L, response.getAccountId());
        assertEquals("FinGenie Bank", response.getBankName());
    }

    // ✅ createAccount - max limit exceeded
    @Test
    void createAccount_shouldThrowException_whenLimitExceeded() {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.SAVINGS);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(accountRepository.countByUserAndAccountType(user, AccountType.SAVINGS))
                .thenReturn(3L);

        assertThrows(BusinessException.class,
                () -> accountService.createAccount(request));
    }

    // ✅ getAccountById - success
    @Test
    void getAccountById_shouldReturnAccount() {

        Account account = Account.builder()
                .accountId(1L)
                .accountNumber("123456")
                .bankName("FinGenie Bank")
                .ifscCode("FGNB0001234")
                .balance(1000.0)
                .user(user)
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccountById(1L);

        assertEquals(1L, response.getAccountId());
        assertEquals(1000.0, response.getBalance());
    }

    // ✅ getAccountById - null id
    @Test
    void getAccountById_shouldThrowException_whenIdNull() {
        assertThrows(BusinessException.class,
                () -> accountService.getAccountById(null));
    }

    // ✅ getBalance - success
    @Test
    void getBalance_shouldReturnBalance() {

        Account account = Account.builder()
                .accountId(1L)
                .balance(5000.0)
                .user(user)
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        Double balance = accountService.getBalance(1L);

        assertEquals(5000.0, balance);
    }

    // ✅ getMyAccounts
    @Test
    void getMyAccounts_shouldReturnList() {

        Account account = Account.builder()
                .accountId(1L)
                .accountNumber("123")
                .bankName("FinGenie Bank")
                .ifscCode("FGNB0001234")
                .balance(1000.0)
                .accountType(AccountType.SAVINGS)
                .user(user)
                .build();

        when(accountRepository.findByUserEmail("test@gmail.com"))
                .thenReturn(List.of(account));

        List<AccountResponse> list = accountService.getMyAccounts();

        assertEquals(1, list.size());
        assertEquals("123", list.get(0).getAccountNumber());
    }

    // ✅ getAllAccounts
    @Test
    void getAllAccounts_shouldReturnAll() {

        Account acc1 = Account.builder().accountId(1L).accountNumber("111").bankName("FinGenie Bank")
                .ifscCode("FGNB0001234").balance(200.0).accountType(AccountType.SAVINGS).user(user).build();

        Account acc2 = Account.builder().accountId(2L).accountNumber("222").bankName("FinGenie Bank")
                .ifscCode("FGNB0001234").balance(500.0).accountType(AccountType.CURRENT).user(user).build();
        

        when(accountRepository.findAll()).thenReturn(List.of(acc1, acc2));

        List<AccountResponse> result = accountService.getAllAccounts();

        assertEquals(2, result.size());
    }

    // ✅ unauthorized access case
    @Test
    void getAccountById_shouldThrow_whenUnauthorized() {

        User otherUser = new User();
        otherUser.setUserId(2L);
        otherUser.setEmail("other@gmail.com");

        Account account = Account.builder()
                .accountId(1L)
                .user(otherUser)
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertThrows(BusinessException.class,
                () -> accountService.getAccountById(1L));
    }
}