package com.example.omnigym.clinico;

import java.time.LocalDateTime;

public record DocumentoMedicoResponseDTO(
    Long id,
    String tipo,
    String descricao,
    String mimeType,
    Long tamanhoBytes,
    LocalDateTime dataUpload,
    LocalDateTime dataProximaReavaliacao,
    String criadoPor,
    Integer acessosCount,
    Boolean ativo,
    String downloadUrl
) {}
