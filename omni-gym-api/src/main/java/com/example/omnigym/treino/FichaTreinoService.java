package com.example.omnigym.treino;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FichaTreinoService {

    private final FichaTreinoRepository fichaTreinoRepository;
    private final TreinoExercicioRepository treinoExercicioRepository;
    private final UserRepository userRepository;
    private final AlunoPerfilRepository perfilRepository;
    private final ExercicioRepository exercicioRepository;
    private final AcessibilidadeService acessibilidadeService;
    private final LogisticaService logisticaService;

    public FichaTreinoService(FichaTreinoRepository fichaTreinoRepository,
                               TreinoExercicioRepository treinoExercicioRepository,
                               UserRepository userRepository,
                               AlunoPerfilRepository perfilRepository,
                               ExercicioRepository exercicioRepository,
                               AcessibilidadeService acessibilidadeService,
                               LogisticaService logisticaService) {
        this.fichaTreinoRepository = fichaTreinoRepository;
        this.treinoExercicioRepository = treinoExercicioRepository;
        this.userRepository = userRepository;
        this.perfilRepository = perfilRepository;
        this.exercicioRepository = exercicioRepository;
        this.acessibilidadeService = acessibilidadeService;
        this.logisticaService = logisticaService;
    }

    @Transactional
    public FichaTreinoResponseDTO cadastrarFichaTreino(Long alunoId, String instrutorUsername, FichaTreinoDTO dto) {
        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado com ID: " + alunoId));

        if (aluno.getRole() != Role.ROLE_ALUNO) {
            throw new IllegalArgumentException("O usuário de destino deve possuir a role ROLE_ALUNO.");
        }

        User instrutor = userRepository.findByUsername(instrutorUsername)
                .orElseThrow(() -> new IllegalArgumentException("Instrutor não encontrado com o username: " + instrutorUsername));

        // Desativa fichas anteriores
        List<FichaTreino> anteriores = fichaTreinoRepository.findByAlunoIdAndAtivaTrue(alunoId);
        for (FichaTreino f : anteriores) {
            f.setAtiva(false);
            fichaTreinoRepository.save(f);
        }

        FichaTreino ficha = new FichaTreino(aluno, instrutor, dto.nome());
        ficha.setAtiva(true);
        ficha.setDataCriacao(new Date());

        List<TreinoExercicio> itens = new ArrayList<>();
        for (TreinoExercicioDTO itemDTO : dto.exercicios()) {
            Exercicio exercicio = exercicioRepository.findById(itemDTO.exercicioId())
                    .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado com ID: " + itemDTO.exercicioId()));

            TreinoExercicio item = new TreinoExercicio(
                ficha,
                exercicio,
                itemDTO.series(),
                itemDTO.repeticoes(),
                itemDTO.cargaInicial(),
                itemDTO.descansoSegundos(),
                itemDTO.ordemExecucao()
            );
            itens.add(item);
        }
        ficha.setExercicios(itens);

        FichaTreino saved = fichaTreinoRepository.save(ficha);
        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public FichaTreinoResponseDTO obterTreinoDiario(String alunoUsername) {
        User aluno = userRepository.findByUsername(alunoUsername)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado com username: " + alunoUsername));

        AlunoPerfil perfil = perfilRepository.findByUserId(aluno.getId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil de matrícula não preenchido para o aluno."));

        if (perfil.getStatusMatricula() != StatusMatricula.HOMOLOGADA) {
            throw new IllegalStateException("A matrícula do aluno deve estar HOMOLOGADA para acessar o treino diário.");
        }

        List<FichaTreino> ativas = fichaTreinoRepository.findByAlunoIdAndAtivaTrue(aluno.getId());
        if (ativas.isEmpty()) {
            throw new IllegalArgumentException("Nenhum treino ativo cadastrado para o aluno.");
        }
        FichaTreino ficha = ativas.get(0);

        // Mapear exercícios originais
        List<TreinoExercicio> originalItens = ficha.getExercicios();
        List<Exercicio> originalExercicios = originalItens.stream()
                .map(TreinoExercicio::getExercicio)
                .collect(Collectors.toList());

        // 1. Filtrar pelo Motor de Acessibilidade
        List<AcessibilidadeService.ResultadoAcessibilidade> classificados = 
                acessibilidadeService.classificarExercicios(perfil, originalExercicios);

        // Filtra para manter apenas Liberados ou Adaptados (exclui Bloqueados)
        List<AcessibilidadeService.ResultadoAcessibilidade> permitidos = classificados.stream()
                .filter(res -> res.status() != AcessibilidadeService.StatusAcessibilidade.BLOQUEADO)
                .collect(Collectors.toList());

        // 2. Ordenar pelo Motor Logístico (Estação de Trabalho Única)
        List<AcessibilidadeService.ResultadoAcessibilidade> ordenados = 
                logisticaService.otimizarOrdem(permitidos);

        // 3. Mapear para DTOs combinando os dados do TreinoExercicio original com as instruções de Acessibilidade
        Map<Long, TreinoExercicio> originalItensMap = originalItens.stream()
                .collect(Collectors.toMap(te -> te.getExercicio().getId(), te -> te));

        List<TreinoExercicioResponseDTO> exerciciosResponse = ordenados.stream()
                .map(res -> {
                    Exercicio ex = res.exercicio();
                    TreinoExercicio te = originalItensMap.get(ex.getId());

                    List<String> obsText = te.getObservacoes() != null ? 
                        te.getObservacoes().stream().map(ObservacaoPedagogica::getTexto).collect(Collectors.toList()) : 
                        Collections.emptyList();

                    return new TreinoExercicioResponseDTO(
                        te.getId(),
                        ex.getId(),
                        ex.getNome(),
                        ex.getEstacaoTrabalho(),
                        te.getSeries(),
                        te.getRepeticoes(),
                        te.getCargaInicial(),
                        te.getDescansoSegundos(),
                        te.getOrdemExecucao(),
                        res.status().name(),
                        res.acessorio() != null ? res.acessorio().getNome() : null,
                        res.instrucaoTexto(),
                        obsText
                    );
                })
                .collect(Collectors.toList());

        return new FichaTreinoResponseDTO(
            ficha.getId(),
            aluno.getId(),
            aluno.getName(),
            ficha.getInstrutor().getId(),
            ficha.getInstrutor().getName(),
            ficha.getNome(),
            ficha.getAtiva(),
            ficha.getDataCriacao(),
            exerciciosResponse
        );
    }

    private FichaTreinoResponseDTO mapToResponseDTO(FichaTreino ficha) {
        List<TreinoExercicioResponseDTO> exercicios = ficha.getExercicios().stream()
                .map(te -> {
                    List<String> obsText = te.getObservacoes() != null ? 
                        te.getObservacoes().stream().map(ObservacaoPedagogica::getTexto).collect(Collectors.toList()) : 
                        Collections.emptyList();

                    return new TreinoExercicioResponseDTO(
                        te.getId(),
                        te.getExercicio().getId(),
                        te.getExercicio().getNome(),
                        te.getExercicio().getEstacaoTrabalho(),
                        te.getSeries(),
                        te.getRepeticoes(),
                        te.getCargaInicial(),
                        te.getDescansoSegundos(),
                        te.getOrdemExecucao(),
                        "LIBERADO", // Default para criação crua
                        null,
                        null,
                        obsText
                    );
                })
                .collect(Collectors.toList());

        return new FichaTreinoResponseDTO(
            ficha.getId(),
            ficha.getAluno().getId(),
            ficha.getAluno().getName(),
            ficha.getInstrutor().getId(),
            ficha.getInstrutor().getName(),
            ficha.getNome(),
            ficha.getAtiva(),
            ficha.getDataCriacao(),
            exercicios
        );
    }
}
