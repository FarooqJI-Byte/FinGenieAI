package com.fingenie.ai.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

	@NotNull(message = "From account ID is required")
	private Long fromAccountId;

	@NotNull(message = "To account ID is required")
	private Long toAccountId;

	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be greater than zero")
	private Double amount;

}
