package com.example.auth.treino;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LogisticaService {

    /**
     * Reordena os resultados de acessibilidade para agrupar consecutivamente os exercícios
     * que compartilham a mesma estação de trabalho, minimizando os deslocamentos no salão.
     */
    public List<AcessibilidadeService.ResultadoAcessibilidade> otimizarOrdem(
            List<AcessibilidadeService.ResultadoAcessibilidade> exercicios) {
        if (exercicios == null || exercicios.isEmpty()) {
            return Collections.emptyList();
        }

        // Usamos LinkedHashMap para manter a ordem da primeira aparição de cada estação de trabalho
        Map<String, List<AcessibilidadeService.ResultadoAcessibilidade>> grupos = new LinkedHashMap<>();

        for (AcessibilidadeService.ResultadoAcessibilidade res : exercicios) {
            String estacao = res.exercicio().getEstacaoTrabalho();
            // Evita chaves nulas ou vazias
            String chave = (estacao != null && !estacao.isBlank()) ? estacao.trim().toUpperCase() : "OUTROS";
            
            grupos.computeIfAbsent(chave, k -> new ArrayList<>()).add(res);
        }

        List<AcessibilidadeService.ResultadoAcessibilidade> ordenados = new ArrayList<>();
        for (List<AcessibilidadeService.ResultadoAcessibilidade> grupo : grupos.values()) {
            ordenados.addAll(grupo);
        }

        return ordenados;
    }
}
