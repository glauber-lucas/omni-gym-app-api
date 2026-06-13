package com.example.auth.matricula;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

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
