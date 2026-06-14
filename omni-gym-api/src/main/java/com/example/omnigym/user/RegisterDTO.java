package com.example.omnigym.user;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank
        @Email
        String usuario,
        @NotBlank
        @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres")
        String senha,
        String role,
        String instructorSecret
 ) {}
