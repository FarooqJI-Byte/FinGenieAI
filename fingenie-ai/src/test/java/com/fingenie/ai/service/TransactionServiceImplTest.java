package com.fingenie.ai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fingenie.ai.dto.DepositRequest;
import com.fingenie.ai.dto.TransactionResultResponse;
import com.fingenie.ai.dto.TransferRequest;
import com.fingenie.ai.dto.WithdrawRequest;
import com.fingenie.ai.entity.Account;
import com.fingenie.ai.entity.Transaction;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.repository.AccountRepository;
import com.fingenie.ai.repository.TransactionRepository;
import com.fingenie.ai.repository.UserRepository;

class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    private User user;
    private Account account;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .userId(1L)
                .email("test@example.com")
                .build();

        account = Account.builder()
                .accountId(100L)
                .balance(50000.0)
                .user(user)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@example.com", null)
        );

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    }

    // ✅ DEPOSIT SUCCESS
    @Test
    void testDepositSuccess() {
        DepositRequest request = new DepositRequest(100L, 10000.0);

        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));

        TransactionResultResponse response = transactionService.deposit(request);

        assertEquals(60000.0, response.getCurrentBalance());
        assertEquals("Amount deposited successfully", response.getMessage());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // ✅ DEPOSIT INVALID AMOUNT
    @Test
    void testDepositInvalidAmount() {
        DepositRequest request = new DepositRequest(100L, -500.0);

        assertThrows(BusinessException.class, () -> {
            transactionService.deposit(request);
        });
    }

    // ✅ WITHDRAW SUCCESS
    @Test
    void testWithdrawSuccess() {
        WithdrawRequest request = new WithdrawRequest(100L, 10000.0);

        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));

        TransactionResultResponse response = transactionService.withdraw(request);

        assertEquals(40000.0, response.getCurrentBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // ✅ WITHDRAW INSUFFICIENT BALANCE
    @Test
    void testWithdrawInsufficientBalance() {
        WithdrawRequest request = new WithdrawRequest(100L, 100000.0);

        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));

        assertThrows(BusinessException.class, () -> {
            transactionService.withdraw(request);
        });

        verify(transactionRepository, times(1)).save(any(Transaction.class)); // FAILED txn saved
    }

    // ✅ TRANSFER SUCCESS
    @Test
    void testTransferSuccess() {
        Account receiver = Account.builder()
                .accountId(200L)
                .balance(20000.0)
                .user(user)
                .build();

        TransferRequest request = new TransferRequest(100L, 200L, 5000.0);

        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(200L)).thenReturn(Optional.of(receiver));

        TransactionResultResponse response = transactionService.transfer(request);

        assertEquals(45000.0, response.getCurrentBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // ✅ TRANSFER FAILURE (INSUFFICIENT)
    @Test
    void testTransferInsufficientBalance() {
        Account receiver = Account.builder()
                .accountId(200L)
                .balance(20000.0)
                .user(user)
                .build();

        TransferRequest request = new TransferRequest(100L, 200L, 100000.0);

        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(200L)).thenReturn(Optional.of(receiver));

        assertThrows(BusinessException.class, () -> {
            transactionService.transfer(request);
        });

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // ✅ FRAUD DETECTION TRIGGER
    @Test
    void testFraudDetectionTriggersEmail() {
        DepositRequest request = new DepositRequest(100L, 200000.0); // high amount

        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));

        transactionService.deposit(request);

        verify(emailService, times(1))
                .sendFraudAlert(eq("test@example.com"), eq(200000.0));
    }

    // ✅ UNAUTHORIZED ACCESS
    @Test
    void testUnauthorizedAccess() {
        User anotherUser = User.builder()
                .userId(2L)
                .email("other@example.com")
                .build();

        account.setUser(anotherUser);

        when(accountRepository.findById(100L)).thenReturn(Optional.of(account));

        WithdrawRequest request = new WithdrawRequest(100L, 1000.0);

        assertThrows(BusinessException.class, () -> {
            transactionService.withdraw(request);
        });
    }
}