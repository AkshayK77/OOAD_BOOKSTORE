package com.bookstore.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final String message;
    private final int statusCode;

    public BaseException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
} 