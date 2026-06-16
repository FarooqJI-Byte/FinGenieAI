package com.fingenie.ai.repository;

import com.fingenie.ai.entity.Account;
import com.fingenie.ai.entity.User;
import com.fingenie.ai.enums.AccountType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);
    long countByUserAndAccountType(User user, AccountType accountType);
    List<Account> findByUser(User user);
	List<Account> findByUserEmail(String email);
}