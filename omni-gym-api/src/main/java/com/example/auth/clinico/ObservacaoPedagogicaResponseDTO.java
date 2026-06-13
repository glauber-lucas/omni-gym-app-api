package com.example.auth.clinico;

import java.util.Date;

public record ObservacaoPedagogicaResponseDTO(
    Long id,
    Long treinoExercicioId,
    String texto,
    Date dataCriacao
) {}
