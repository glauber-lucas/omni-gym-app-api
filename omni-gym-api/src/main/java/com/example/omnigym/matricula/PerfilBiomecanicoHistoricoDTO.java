package com.example.omnigym.matricula;

import java.time.LocalDateTime;
import java.util.List;

public record PerfilBiomecanicoHistoricoDTO(
    Long id,
    String estabilidadeTronco,
    Boolean bloqueioMedico,
    List<String> restricoes,
    LocalDateTime dataCriacao,
    LocalDateTime dataReavaliacao,
    String observacoes
) {}
