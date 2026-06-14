package com.example.omnigym.clinico;

import java.time.LocalDateTime;
import java.util.List;

public record DocumentoMedicoUploadDTO(
    String tipo,
    String descricao,
    LocalDateTime dataProximaReavaliacao
) {}
