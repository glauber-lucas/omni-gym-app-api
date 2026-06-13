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
public class AcessibilidadeService {

    private final ExercicioAdaptacaoRepository adaptacaoRepository;

    public AcessibilidadeService(ExercicioAdaptacaoRepository adaptacaoRepository) {
        this.adaptacaoRepository = adaptacaoRepository;
    }

    public enum StatusAcessibilidade {
        LIBERADO,
        LIBERADO_COM_ADAPTACAO,
        BLOQUEADO
    }

    public record ResultadoAcessibilidade(
        Exercicio exercicio,
        StatusAcessibilidade status,
        Acessorio acessorio,
        String instrucaoTexto
    ) {}

    public List<ResultadoAcessibilidade> classificarExercicios(AlunoPerfil perfil, List<Exercicio> exercicios) {
        if (perfil.getBloqueioMedico() != null && perfil.getBloqueioMedico()) {
            throw new IllegalStateException("Usuário possui contraindicação médica absoluta.");
        }

        List<ResultadoAcessibilidade> resultados = new ArrayList<>();

        for (Exercicio ex : exercicios) {
            resultados.add(classificarExercicio(perfil, ex));
        }

        return resultados;
    }

    private ResultadoAcessibilidade classificarExercicio(AlunoPerfil perfil, Exercicio ex) {
        // 1. Validar estabilidade de tronco
        EstabilidadeTronco troncoAluno = perfil.getEstabilidadeTronco();
        EstabilidadeTronco troncoExercicio = ex.getEstabilidadeTroncoMinima();

        if (troncoAluno != null && troncoExercicio != null) {
            if (!isEstabilidadeSuficiente(troncoAluno, troncoExercicio)) {
                return new ResultadoAcessibilidade(ex, StatusAcessibilidade.BLOQUEADO, null, null);
            }
        }

        // 2. Validar colisão de articulações (Exigências vs Restrições)
        Set<Articulacao> restricoes = perfil.getRestricoes() != null ? perfil.getRestricoes() : Collections.emptySet();
        Set<Articulacao> exigencias = ex.getExigencias() != null ? ex.getExigencias() : Collections.emptySet();

        Set<Articulacao> colisoes = new HashSet<>(exigencias);
        colisoes.retainAll(restricoes);

        if (colisoes.isEmpty()) {
            return new ResultadoAcessibilidade(ex, StatusAcessibilidade.LIBERADO, null, null);
        }

        // Se houver colisões, precisamos encontrar adaptações para TODAS as articulações em colisão
        Acessorio acessorioSelecionado = null;
        StringBuilder instrucoesAcumuladas = new StringBuilder();

        for (Articulacao art : colisoes) {
            List<ExercicioAdaptacao> adaptacoes = adaptacaoRepository.findByExercicioIdAndArticulacaoId(ex.getId(), art.getId());
            if (adaptacoes.isEmpty()) {
                // Colisão sem adaptação mapeada -> Bloqueia o exercício
                return new ResultadoAcessibilidade(ex, StatusAcessibilidade.BLOQUEADO, null, null);
            }
            
            // Pega a primeira adaptação disponível para simplificar
            ExercicioAdaptacao adaptacao = adaptacoes.get(0);
            acessorioSelecionado = adaptacao.getAcessorio();
            if (instrucoesAcumuladas.length() > 0) {
                instrucoesAcumuladas.append(" | ");
            }
            instrucoesAcumuladas.append(adaptacao.getInstrucaoTexto());
        }

        return new ResultadoAcessibilidade(
            ex, 
            StatusAcessibilidade.LIBERADO_COM_ADAPTACAO, 
            acessorioSelecionado, 
            instrucoesAcumuladas.toString()
        );
    }

    private boolean isEstabilidadeSuficiente(EstabilidadeTronco aluno, EstabilidadeTronco exercicio) {
        if (aluno == EstabilidadeTronco.PLENO) return true;
        if (aluno == EstabilidadeTronco.PARCIAL) return exercicio != EstabilidadeTronco.PLENO;
        if (aluno == EstabilidadeTronco.LIMITADO) return exercicio == EstabilidadeTronco.LIMITADO;
        return false;
    }
}
