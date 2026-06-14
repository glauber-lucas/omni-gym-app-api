package com.example.omnigym.financeiro;

import java.util.Date;
import java.util.List;

public record AssinaturaResponseDTO(
    Long id,
    Long alunoId,
    String alunoNome,
    Long planoId,
    String planoNome,
    Integer duracaoMeses,
    Date dataInicio,
    Date dataFim,
    String status,
    Integer faturasGeradas,
    List<FaturaResponseDTO> faturas
) {}
