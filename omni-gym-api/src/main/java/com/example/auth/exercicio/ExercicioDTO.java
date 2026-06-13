package com.example.auth.exercicio;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ExercicioDTO(
        @NotBlank(message = "Nome do exercício é obrigatório")
        String nome,

        @NotBlank(message = "Grupo muscular é obrigatório")
        String grupoMuscular,

        @NotBlank(message = "Estação de trabalho é obrigatória")
        String estacaoTrabalho,

        @NotBlank(message = "Estabilidade mínima de tronco é obrigatória")
        String estabilidadeTroncoMinima,

        @NotEmpty(message = "O exercício deve possuir ao menos uma articulação exigida")
        List<Long> exigenciasIds,

        List<ExercicioAdaptacaoDTO> adaptacoes
) {}
