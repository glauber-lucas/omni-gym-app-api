package com.example.auth.matricula;

import jakarta.validation.constraints.NotBlank;

public record MatriculaDTO(
        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotBlank(message = "Endereço é obrigatório")
        String endereco,

        @NotBlank(message = "Contato de emergência é obrigatório")
        String contatoEmergencia,

        String infoFamiliar,
        String medicamentos,
        String deficiencias,
        String alergias
) {}
