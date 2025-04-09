package com.bookstore.dto.error;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> errors;

    public ValidationErrorResponse(Map<String, String> errors) {
        this.status = 400;
        this.message = "Validation failed";
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }
} 