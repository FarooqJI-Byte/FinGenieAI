package com.fingenie.ai.service;

import com.fingenie.ai.dto.*;
import com.fingenie.ai.entity.Account;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TransactionServiceImpl service;

    private Account account;
    private User user;

    @BeforeEach
    void setup() {

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@gmail.com");

        account = new Account();
        account.setAccountId(1L);
        account.setBalance(10000.0);
        account.setUser(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@gmail.com", null)
        );

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));
    }

    // ✅ DEPOSIT SUCCESS
    @Test
    void deposit_success() {

        DepositRequest request = new DepositRequest();
        request.setAccountId(1L);
        request.setAmount(1000.0);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        TransactionResultResponse res = service.deposit(request);

        assertEquals(11000.0, res.getCurrentBalance());
    }

    // ✅ WITHDRAW SUCCESS
    @Test
    void withdraw_success() {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(1L);
        request.setAmount(2000.0);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        TransactionResultResponse res = service.withdraw(request);

        assertEquals(8000.0, res.getCurrentBalance());
    }

    // ✅ TRANSFER SUCCESS
    @Test
    void transfer_success() {

        Account receiver = new Account();
        receiver.setAccountId(2L);
        receiver.setBalance(5000.0);
        receiver.setUser(new User());

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(1000.0);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));
        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(receiver));

        TransactionResultResponse res = service.transfer(request);

        assertEquals(9000.0, res.getCurrentBalance());
    }
}
