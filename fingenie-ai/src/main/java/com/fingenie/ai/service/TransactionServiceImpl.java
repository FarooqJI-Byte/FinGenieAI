package com.fingenie.ai.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fingenie.ai.dto.*;
import com.fingenie.ai.entity.Account;
import com.fingenie.ai.entity.Transaction;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.enums.TransactionStatus;
import com.fingenie.ai.enums.TransactionType;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.exception.ResourceNotFoundException;
import com.fingenie.ai.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final UserRepository userRepository;
	private final EmailService emailService;

	// ✅ DEPOSIT
	@Override
	@Transactional
	public TransactionResultResponse deposit(DepositRequest request) {

		log.info("Deposit request for accountId: {}", request.getAccountId());

		if (request.getAccountId() == null) {
			throw new BusinessException("Account ID is required");
		}

		if (request.getAmount() == null || request.getAmount() <= 0) {
			throw new BusinessException("Amount must be greater than zero");
		}

		if (!Double.isFinite(request.getAmount())) {
			throw new BusinessException("Amount must be a valid number");
		}

		Account account = accountRepository.findById(request.getAccountId())
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));

		validateOwnership(account);

		account.setBalance(account.getBalance() + request.getAmount());
		accountRepository.save(account);

		// ✅ FRAUD LOGIC (SAFE)
		boolean fraud = isFraud(account, request.getAmount());
		double risk = calculateRisk(request.getAmount());

		if (fraud) {
			String email = account.getUser().getEmail();
			emailService.sendFraudAlert(email, request.getAmount());
		}

		transactionRepository.save(Transaction.builder().amount(request.getAmount()).type(TransactionType.DEPOSIT)
				.status(TransactionStatus.SUCCESS).date(LocalDateTime.now()).account(account).fraudFlag(fraud)
				.riskScore(risk).build());

		return new TransactionResultResponse(request.getAmount(), "Amount deposited successfully",
				account.getBalance());
	}

	// ✅ WITHDRAW
	@Override
	@Transactional
	public TransactionResultResponse withdraw(WithdrawRequest request) {

		log.info("Withdraw request for accountId: {}", request.getAccountId());

		if (request.getAccountId() == null) {
			throw new BusinessException("Account ID is required");
		}

		if (request.getAmount() == null || request.getAmount() <= 0) {
			throw new BusinessException("Amount must be greater than zero");
		}

		if (!Double.isFinite(request.getAmount())) {
			throw new BusinessException("Amount must be a valid number");
		}

		Account account = accountRepository.findById(request.getAccountId())
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));

		validateOwnership(account);

		// ✅ FRAUD LOGIC
		boolean fraud = isFraud(account, request.getAmount());
		double risk = calculateRisk(request.getAmount());
		if (fraud) {
			String email = account.getUser().getEmail();
			emailService.sendFraudAlert(email, request.getAmount());
		}

		if (account.getBalance() < request.getAmount()) {

			transactionRepository.save(Transaction.builder().amount(request.getAmount()).type(TransactionType.WITHDRAW)
					.status(TransactionStatus.FAILED).date(LocalDateTime.now()).account(account).fraudFlag(fraud)
					.riskScore(risk).build());

			throw new BusinessException("Insufficient balance");
		}

		account.setBalance(account.getBalance() - request.getAmount());
		accountRepository.save(account);

		transactionRepository.save(Transaction.builder().amount(request.getAmount()).type(TransactionType.WITHDRAW)
				.status(TransactionStatus.SUCCESS).date(LocalDateTime.now()).account(account).fraudFlag(fraud)
				.riskScore(risk).build());

		return new TransactionResultResponse(request.getAmount(), "Amount withdrawn successfully",
				account.getBalance());
	}

	// ✅ TRANSFER
	@Override
	@Transactional
	public TransactionResultResponse transfer(TransferRequest request) {

		log.info("Transfer request from {} to {}", request.getFromAccountId(), request.getToAccountId());

		if (request.getFromAccountId() == null || request.getToAccountId() == null) {
			throw new BusinessException("Both account IDs are required");
		}

		if (request.getAmount() == null || request.getAmount() <= 0) {
			throw new BusinessException("Amount must be greater than zero");
		}

		if (!Double.isFinite(request.getAmount())) {
			throw new BusinessException("Amount must be a valid number");
		}

		if (request.getFromAccountId().equals(request.getToAccountId())) {
			throw new BusinessException("Cannot transfer to the same account");
		}

		Account sender = accountRepository.findById(request.getFromAccountId())
				.orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

		Account receiver = accountRepository.findById(request.getToAccountId())
				.orElseThrow(() -> new ResourceNotFoundException("Receiver account not found"));

		validateOwnership(sender);

		// ✅ FRAUD LOGIC
		boolean fraud = isFraud(sender, request.getAmount());
		double risk = calculateRisk(request.getAmount());

		if (fraud) {
			String email = sender.getUser().getEmail();
			emailService.sendFraudAlert(email, request.getAmount());
		}

		if (sender.getBalance() < request.getAmount()) {

			transactionRepository.save(Transaction.builder().amount(request.getAmount()).type(TransactionType.TRANSFER)
					.status(TransactionStatus.FAILED).date(LocalDateTime.now()).account(sender).fraudFlag(fraud)
					.riskScore(risk).build());

			throw new BusinessException("Insufficient balance");
		}

		sender.setBalance(sender.getBalance() - request.getAmount());
		receiver.setBalance(receiver.getBalance() + request.getAmount());

		accountRepository.save(sender);
		accountRepository.save(receiver);

		transactionRepository.save(Transaction.builder().amount(request.getAmount()).type(TransactionType.TRANSFER)
				.status(TransactionStatus.SUCCESS).date(LocalDateTime.now()).account(sender).fraudFlag(fraud)
				.riskScore(risk).build());

		return new TransactionResultResponse(request.getAmount(), "Transfer successful", sender.getBalance());
	}

	// ✅ EXISTING METHODS UNCHANGED BELOW ✅

	public List<TransactionResponse> getTransactionHistory(Long accountId) {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
		validateOwnership(account);

		return transactionRepository.findByAccount(account).stream()
				.map(tx -> new TransactionResponse(tx.getTransactionId(), tx.getAmount(), tx.getType(), tx.getStatus(),
						tx.getDate(), tx.getFraudFlag(), tx.getRiskScore()))
				.toList();

	}

	public List<TransactionResponse> getTransactionsByType(Long accountId, TransactionType type) {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
		validateOwnership(account);

		return transactionRepository.findByAccountAndType(account, type).stream()
				.map(tx -> new TransactionResponse(tx.getTransactionId(), tx.getAmount(), tx.getType(), tx.getStatus(),
						tx.getDate(), tx.getFraudFlag(), tx.getRiskScore()))
				.toList();
	}

	public List<TransactionResponse> getTransactionsByDate(Long accountId, LocalDate startDate, LocalDate endDate) {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
		validateOwnership(account);

		return transactionRepository
				.findByAccountAndDateBetween(account, startDate.atStartOfDay(), endDate.atTime(23, 59, 59)).stream()
				.map(tx -> new TransactionResponse(tx.getTransactionId(), tx.getAmount(), tx.getType(), tx.getStatus(),
						tx.getDate(), tx.getFraudFlag(), tx.getRiskScore()))
				.toList();
	}

	private void validateOwnership(Account account) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (!account.getUser().getUserId().equals(user.getUserId())) {
			throw new BusinessException("You are not authorized to access this account");
		}
	}

	// ✅ FRAUD METHODS (UNCHANGED)

	private double calculateRisk(double amount) {
		if (amount > 100000)
			return 0.9;
		if (amount > 50000)
			return 0.7;
		if (amount > 20000)
			return 0.5;
		return 0.2;
	}

	private boolean isFraud(Account account, double amount) {

		if (amount > account.getBalance() * 0.8) {
			return true;
		}

		double risk = calculateRisk(amount);
		return risk >= 0.7;
	}

}
