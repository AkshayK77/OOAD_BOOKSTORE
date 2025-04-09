package com.bookstore.dto.auth;

public record AuthenticationRequest(
    String email,
    String password
) {} 