package com.example.omnigym.financeiro;

import java.math.BigDecimal;
import java.util.Date;

public record FaturaResponseDTO(
    Long id,
    Long alunoId,
    String alunoNome,
    Long planoId,
    String planoNome,
    BigDecimal valorOriginal,
    BigDecimal desconto,
    BigDecimal valorCobrado, // valorOriginal - desconto
    BigDecimal valorPago,
    Date dataVencimento,
    Date dataPagamento,
    String status
) {}
