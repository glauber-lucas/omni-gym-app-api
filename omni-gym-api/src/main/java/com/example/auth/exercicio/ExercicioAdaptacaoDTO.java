package com.example.auth.exercicio;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExercicioAdaptacaoDTO(
        @NotNull(message = "Articulação em conflito é obrigatória")
        Long articulacaoId,

        @NotNull(message = "Acessório predeterminado é obrigatório")
        Long acessorioId,

        @NotBlank(message = "Instrução de texto é obrigatória")
        String instrucaoTexto
) {}
