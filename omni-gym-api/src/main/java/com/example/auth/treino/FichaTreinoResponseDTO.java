package com.example.auth.treino;

import java.util.Date;
import java.util.List;

public record FichaTreinoResponseDTO(
    Long id,
    Long alunoId,
    String alunoNome,
    Long instrutorId,
    String instrutorNome,
    String nome,
    Boolean ativa,
    Date dataCriacao,
    List<TreinoExercicioResponseDTO> exercicios
) {}
