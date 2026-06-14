package com.example.auth.matricula;

import java.util.List;

public record AlunoPerfilResponseDTO(
        Long id,
        Long userId,
        String name,
        String documentId,
        String username,
        String telefone,
        String endereco,
        String contatoEmergencia,
        String infoFamiliar,
        String medicamentos,
        String deficiencias,
        String alergias,
        String statusMatricula,
        String estabilidadeTronco,
        Boolean bloqueioMedico,
        List<String> restricoes
) {}
