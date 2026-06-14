package com.example.omnigym.financeiro;

import jakarta.validation.constraints.NotNull;

public record AssinaturaRequestDTO(
    @NotNull(message = "planoId é obrigatório")
    Long planoId
) {}
