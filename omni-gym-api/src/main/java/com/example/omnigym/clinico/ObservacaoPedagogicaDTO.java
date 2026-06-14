package com.example.omnigym.clinico;

import jakarta.validation.constraints.NotBlank;

public record ObservacaoPedagogicaDTO(
    @NotBlank(message = "Texto da observação é obrigatório")
    String texto
) {}
