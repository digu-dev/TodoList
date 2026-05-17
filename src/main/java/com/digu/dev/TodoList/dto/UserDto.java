package com.digu.dev.TodoList.dto;

import java.util.UUID;

import com.digu.dev.TodoList.model.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(
        UUID id,
        @NotBlank(message = "Username is required")
        String username,
        @NotBlank(message = "Password is required")
        String password,
        @NotNull(message = "Role is required")
        UserRole role) {
}
