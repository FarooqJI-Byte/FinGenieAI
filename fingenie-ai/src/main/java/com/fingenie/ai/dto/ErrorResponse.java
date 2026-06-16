package com.fingenie.ai.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {

    private boolean success;
    private int status;
    private String message;
    private Map<String, String> errors;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.success = false;
        this.status = status;
        this.message = message;
        this.errors = Map.of();
        this.timestamp = timestamp;
    }

    public ErrorResponse(int status, String message, Map<String, String> errors, LocalDateTime timestamp) {
        this.success = false;
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }
}
