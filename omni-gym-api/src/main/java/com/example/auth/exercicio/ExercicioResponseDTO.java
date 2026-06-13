package com.example.auth.exercicio;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import java.util.List;

public record ExercicioResponseDTO(
        Long id,
        String nome,
        String grupoMuscular,
        String estacaoTrabalho,
        String estabilidadeTroncoMinima,
        List<String> exigencias,
        List<AdaptacaoDetail> adaptacoes
) {
    public record AdaptacaoDetail(
            Long id,
            String articulacao,
            String acessorio,
            String instrucaoTexto
    ) {}
}
