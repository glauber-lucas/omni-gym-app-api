package com.example.omnigym.financeiro;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlanoDTO(
    @NotBlank(message = "Nome do plano é obrigatório")
    String nome,
    
    @NotNull(message = "Valor do plano é obrigatório")
    @Positive(message = "Valor do plano deve ser maior que zero")
    BigDecimal valor,
    
    @NotNull(message = "Duração em meses é obrigatória")
    @Positive(message = "Duração em meses deve ser maior que zero")
    Integer duracaoMeses
) {}
