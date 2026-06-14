package com.example.omnigym.clinico;

import java.util.Date;

public record DossieClinicoResponseDTO(
    Long id,
    Long alunoId,
    String laudoMedicoUrl,
    String observacoes,
    Date dataAvaliacao,
    Date dataProximaReavaliacao
) {}
