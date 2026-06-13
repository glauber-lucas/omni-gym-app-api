package com.example.auth.treino;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record FichaTreinoDTO(
    @NotBlank(message = "Nome da ficha de treino é obrigatório")
    String nome,

    @NotEmpty(message = "A ficha de treino deve conter pelo menos um exercício")
    @Valid
    List<TreinoExercicioDTO> exercicios
) {}
