package com.example.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank
        @Email
        String usuario,
        @NotBlank
        @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres")
        String senha
 ) {}
