package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Standard error response DTO for API endpoints
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
} 