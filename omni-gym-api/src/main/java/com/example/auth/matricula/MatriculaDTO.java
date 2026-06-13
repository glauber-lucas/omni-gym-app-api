package com.example.auth.matricula;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

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
