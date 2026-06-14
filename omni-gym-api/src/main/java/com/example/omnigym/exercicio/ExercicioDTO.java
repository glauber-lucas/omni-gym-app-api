package com.example.omnigym.exercicio;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ExercicioDTO(
        @NotBlank(message = "Nome do exercício é obrigatório")
        String nome,

        @NotBlank(message = "Grupo muscular é obrigatório")
        String grupoMuscular,

        @NotBlank(message = "Estação de trabalho é obrigatória")
        String estacaoTrabalho,

        @NotBlank(message = "Estabilidade mínima de tronco é obrigatória")
        String estabilidadeTroncoMinima,

        @NotEmpty(message = "O exercício deve possuir ao menos uma articulação exigida")
        List<Long> exigenciasIds,

        List<ExercicioAdaptacaoDTO> adaptacoes
) {}
