package com.example.omnigym.financeiro;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.validation.constraints.NotNull;

public record FaturaDTO(
    Long planoId,
    BigDecimal valor, // Usado caso planoId seja nulo (fatura avulsa)
    @NotNull(message = "Data de vencimento é obrigatória")
    Date dataVencimento
) {}
