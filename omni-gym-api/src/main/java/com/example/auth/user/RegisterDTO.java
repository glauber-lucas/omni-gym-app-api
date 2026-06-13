package com.example.auth.user;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

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
