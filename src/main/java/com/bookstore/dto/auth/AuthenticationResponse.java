package com.bookstore.dto.auth;

public record AuthenticationResponse(
    String token,
    String refreshToken
) {} 