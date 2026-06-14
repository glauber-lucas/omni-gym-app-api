package com.example.auth.clinico;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DossieClinicoDTO(
    @NotBlank(message = "URL do laudo médico é obrigatória")
    String laudoMedicoUrl,

    String observacoes,

    @NotNull(message = "Data de avaliação é obrigatória")
    Date dataAvaliacao,

    @NotNull(message = "Data da próxima reavaliação é obrigatória")
    Date dataProximaReavaliacao
) {}
