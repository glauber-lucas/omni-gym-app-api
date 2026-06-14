package com.example.omnigym.treino;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LogisticaService {

    public List<AcessibilidadeService.ResultadoAcessibilidade> otimizarOrdem(
            List<AcessibilidadeService.ResultadoAcessibilidade> exercicios) {
        if (exercicios == null || exercicios.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<AcessibilidadeService.ResultadoAcessibilidade>> grupos = new LinkedHashMap<>();

        for (AcessibilidadeService.ResultadoAcessibilidade res : exercicios) {
            String estacao = res.exercicio().getEstacaoTrabalho();
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
