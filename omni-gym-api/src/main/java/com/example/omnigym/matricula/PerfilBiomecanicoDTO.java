package com.example.omnigym.matricula;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record PerfilBiomecanicoDTO(
        @NotBlank(message = "Estabilidade de tronco é obrigatória")
        String estabilidadeTronco,

        List<Long> restricoesIds,

        Boolean bloqueioMedico
) {}
