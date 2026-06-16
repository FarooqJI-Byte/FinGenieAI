package com.fingenie.loan.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(int status, String message, T data, LocalDateTime timestamp) {
        this.success = status >= 200 && status < 300;
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }
}
