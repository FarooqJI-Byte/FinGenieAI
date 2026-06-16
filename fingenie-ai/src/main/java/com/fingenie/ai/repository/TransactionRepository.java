package com.fingenie.ai.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fingenie.ai.entity.Account;
import com.fingenie.ai.entity.Transaction;
import com.fingenie.ai.enums.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findByAccount(Account account);
	List<Transaction> findByAccountAndType(Account account, TransactionType type);
	List<Transaction> findByAccountAndDateBetween(
	        Account account,
	        LocalDateTime start,
	        LocalDateTime end
	);
}
