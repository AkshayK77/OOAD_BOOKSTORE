package com.bookstore.exception;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message, 403);
    }
} 