package com.example.auth.clinico;

import jakarta.validation.constraints.NotBlank;

public record ObservacaoPedagogicaDTO(
    @NotBlank(message = "Texto da observação é obrigatório")
    String texto
) {}
