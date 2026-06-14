package com.example.omnigym.financeiro;

import jakarta.validation.constraints.NotNull;

public record PagamentoGatewayRequestDTO(
    @NotNull(message = "provedor é obrigatório")
    String provedor,
    String metodo
) {}
