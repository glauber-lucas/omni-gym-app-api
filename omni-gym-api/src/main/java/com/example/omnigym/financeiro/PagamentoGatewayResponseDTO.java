package com.example.omnigym.financeiro;

import java.util.Date;

public record PagamentoGatewayResponseDTO(
    Long id,
    Long faturaId,
    String provedor,
    String status,
    String urlPagamento,
    String idTransacaoExterna,
    Date dataCriacao
) {}
