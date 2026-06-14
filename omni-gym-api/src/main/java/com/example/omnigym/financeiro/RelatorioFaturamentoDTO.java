package com.example.omnigym.financeiro;

import java.math.BigDecimal;

public record RelatorioFaturamentoDTO(
    BigDecimal totalRecebido,
    BigDecimal totalPendente,
    BigDecimal totalAtrasado,
    Long faturasPagasCount,
    Long faturasPendentesCount,
    Long faturasAtrasadasCount,
    Long totalFaturasCount
) {}
