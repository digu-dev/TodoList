package com.digu.dev.TodoList.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String message) {
}
