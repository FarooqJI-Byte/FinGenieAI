package com.fingenie.loan.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fingenie.loan.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> handleUserNotFound(ResourceNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(404, ex.getMessage(), LocalDateTime.now()));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<?> handleBusinessException(BusinessException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(400, ex.getMessage(), LocalDateTime.now()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {

		Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(
						fieldError -> fieldError.getField(),
						fieldError -> fieldError.getDefaultMessage(),
						(existing, replacement) -> existing
				));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(400, "Validation failed", errors, LocalDateTime.now()));
	}
}
