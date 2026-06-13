package com.example.auth.treino;

import java.util.List;

public record TreinoExercicioResponseDTO(
    Long id,
    Long exercicioId,
    String exercicioNome,
    String estacaoTrabalho,
    Integer series,
    Integer repeticoes,
    String cargaInicial,
    Integer descansoSegundos,
    Integer ordemExecucao,
    String statusAcessibilidade,
    String acessorioNecessario,
    String instrucaoAdaptacao,
    List<String> observacoesPedagogicas
) {}
