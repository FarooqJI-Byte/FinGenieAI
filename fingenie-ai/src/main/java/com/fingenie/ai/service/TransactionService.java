package com.fingenie.ai.service;

import java.time.LocalDate;
import java.util.List;

import com.fingenie.ai.dto.DepositRequest;
import com.fingenie.ai.dto.TransactionResponse;
import com.fingenie.ai.dto.TransactionResultResponse;
import com.fingenie.ai.dto.TransferRequest;
import com.fingenie.ai.dto.WithdrawRequest;
import com.fingenie.ai.enums.TransactionType;

public interface TransactionService {

	TransactionResultResponse deposit(DepositRequest request);

	TransactionResultResponse withdraw(WithdrawRequest request);

	TransactionResultResponse transfer(TransferRequest request);

	List<TransactionResponse> getTransactionHistory(Long accountId);

	List<TransactionResponse> getTransactionsByType(Long accountId, TransactionType type);
		
	List<TransactionResponse> getTransactionsByDate(
	        Long accountId,
	        LocalDate startDate,
	        LocalDate endDate);
}