package com.example.auth.exercicio;

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
