package com.example.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LocalAuthDTO(
        @NotBlank
        @Email
        String identifier,
        @NotBlank
        String password,
        boolean requestRefresh
) {}

