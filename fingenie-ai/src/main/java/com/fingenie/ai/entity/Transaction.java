package com.fingenie.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fingenie.ai.enums.TransactionStatus;
import com.fingenie.ai.enums.TransactionType;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;

	private Double amount;
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;

	private LocalDateTime date;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;

	@Builder.Default
	@Column(nullable = false)
	private Boolean fraudFlag = false;

	@Builder.Default
	@Column(nullable = false)
	private Double riskScore = 0.0;

}