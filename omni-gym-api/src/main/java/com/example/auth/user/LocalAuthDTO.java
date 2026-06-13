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

public record LocalAuthDTO(
        @NotBlank
        @Email
        String identifier,
        @NotBlank
        String password,
        boolean requestRefresh
) {}

