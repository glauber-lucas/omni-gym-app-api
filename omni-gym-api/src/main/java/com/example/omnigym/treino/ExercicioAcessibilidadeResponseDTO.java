package com.example.omnigym.treino;

import java.util.List;

public record ExercicioAcessibilidadeResponseDTO(
    Long exercicioId,
    String nome,
    String grupoMuscular,
    String estacaoTrabalho,
    String statusAcessibilidade,
    String acessorioNecessario,
    String instrucaoAdaptacao,
    List<String> exigenciasArticulares
) {}
