package com.fingenie.ai.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fingenie.ai.dto.AccountResponse;
import com.fingenie.ai.dto.CreateAccountRequest;
import com.fingenie.ai.entity.Account;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.exception.ResourceNotFoundException;
import com.fingenie.ai.repository.AccountRepository;
import com.fingenie.ai.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	@Override
	public AccountResponse createAccount(CreateAccountRequest request) {

		// ✅ get logged-in user from JWT
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		log.info("Creating account for email: {}", email);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		long count = accountRepository.countByUserAndAccountType(user, request.getAccountType());

		if (count >= 3) {
			log.warn("User {} exceeded limit for {}", user.getUserId(), request.getAccountType());
			throw new BusinessException("Maximum 3 accounts of this type allowed");
		}

		// ✅ Generate unique account number
		String accountNumber;
		do {
			accountNumber = String.valueOf((long) (Math.random() * 900000000000L) + 100000000000L);
		} while (accountRepository.findByAccountNumber(accountNumber).isPresent());

		// ✅ Set bank details
		String bankName = "FinGenie Bank";
		String ifscCode = "FGNB0001234";

		// ✅ Create account
		Account account = Account.builder().accountNumber(accountNumber).bankName(bankName).ifscCode(ifscCode)
				.accountType(request.getAccountType()).balance(0.0).user(user).build();

		Account saved = accountRepository.save(account);

		log.info("Account created successfully with id {}", saved.getAccountId());

		return AccountResponse.builder().accountId(saved.getAccountId()).accountNumber(saved.getAccountNumber())
				.bankName(saved.getBankName()).ifscCode(saved.getIfscCode()).accountType(saved.getAccountType())
				.balance(saved.getBalance()).build();
	}

	@Override
	public AccountResponse getAccountById(Long accountId) {

		if (accountId == null) {
			throw new BusinessException("Account ID is required");
		}

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));

		validateOwnership(account);

		return AccountResponse.builder().accountId(account.getAccountId()).accountNumber(account.getAccountNumber())
				.bankName(account.getBankName()).ifscCode(account.getIfscCode()).accountType(account.getAccountType())
				.balance(account.getBalance()).build();
	}

	@Override
	public List<AccountResponse> getAccountsByUser(Long userId) {

		log.info("Fetching all accounts for userId: {}", userId);

		if (userId == null) {
			throw new BusinessException("User ID is required");
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		List<Account> accounts = accountRepository.findByUser(user);

		if (accounts.isEmpty()) {
			log.warn("No accounts found for userId: {}", userId);
			throw new BusinessException("No accounts found for this user");
		}

		List<AccountResponse> response = accounts.stream()
				.map(account -> AccountResponse.builder().accountId(account.getAccountId())
						.accountNumber(account.getAccountNumber()).bankName(account.getBankName())
						.ifscCode(account.getIfscCode()).accountType(account.getAccountType())
						.balance(account.getBalance()).build())
				.toList();

		log.info("Fetched {} accounts for userId: {}", response.size(), userId);

		return response;
	}

	@Override
	public Double getBalance(Long accountId) {

		log.info("Fetching balance for accountId: {}", accountId);

		if (accountId == null) {
			throw new BusinessException("Account ID is required");
		}

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));

		validateOwnership(account);

		log.info("Balance for accountId {} is {}", accountId, account.getBalance());

		return account.getBalance();
	}

	@Override
	public List<AccountResponse> getMyAccounts() {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		List<Account> accounts = accountRepository.findByUserEmail(email);

		return accounts.stream()

				.map(this::mapToResponse).toList();
	}
	@Override
	public List<AccountResponse> getAllAccounts() {

	    log.info("Fetching all accounts for admin");

	    List<Account> accounts = accountRepository.findAll();

	    return accounts.stream()
	            .map(this::mapToResponse)
	            .toList();
	}


	private AccountResponse mapToResponse(Account account) {
		return AccountResponse.builder().accountId(account.getAccountId()).accountNumber(account.getAccountNumber())
				.bankName(account.getBankName()).ifscCode(account.getIfscCode()).accountType(account.getAccountType())
				.balance(account.getBalance()).build();
	}

	private void validateOwnership(Account account) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		if (account.getUser() == null || !email.equals(account.getUser().getEmail())) {
			throw new BusinessException("You are not authorized to access this account");
		}
	}
	
}
