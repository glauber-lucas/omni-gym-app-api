package com.example.omnigym.treino;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TreinoExercicioDTO(
    @NotNull(message = "ID do exercício é obrigatório")
    Long exercicioId,

    @NotNull(message = "Número de séries é obrigatório")
    @Min(value = 1, message = "O número de séries deve ser pelo menos 1")
    Integer series,

    @NotNull(message = "Número de repetições é obrigatório")
    @Min(value = 1, message = "O número de repetições deve ser pelo menos 1")
    Integer repeticoes,

    String cargaInicial,

    @Min(value = 0, message = "O tempo de descanso não pode ser negativo")
    Integer descansoSegundos,

    @NotNull(message = "Ordem de execução é obrigatória")
    Integer ordemExecucao
) {}
