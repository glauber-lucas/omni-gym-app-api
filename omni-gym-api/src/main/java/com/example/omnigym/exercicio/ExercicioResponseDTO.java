package com.example.omnigym.exercicio;

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
