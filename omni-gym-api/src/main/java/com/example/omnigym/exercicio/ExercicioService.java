package com.example.omnigym.exercicio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.omnigym.matricula.EstabilidadeTronco;

@Service
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;
    private final ExercicioAdaptacaoRepository adaptacaoRepository;
    private final ArticulacaoRepository articulacaoRepository;
    private final AcessorioRepository acessorioRepository;

    public ExercicioService(ExercicioRepository exercicioRepository,
                            ExercicioAdaptacaoRepository adaptacaoRepository,
                            ArticulacaoRepository articulacaoRepository,
                            AcessorioRepository acessorioRepository) {
        this.exercicioRepository = exercicioRepository;
        this.adaptacaoRepository = adaptacaoRepository;
        this.articulacaoRepository = articulacaoRepository;
        this.acessorioRepository = acessorioRepository;
    }

    @Transactional
    public ExercicioResponseDTO cadastrarExercicio(ExercicioDTO dto) {
        if (dto.exigenciasIds() == null || dto.exigenciasIds().isEmpty()) {
            throw new IllegalArgumentException("O exercício deve possuir ao menos uma articulação exigida.");
        }

        EstabilidadeTronco estabilidade;
        try {
            estabilidade = EstabilidadeTronco.valueOf(dto.estabilidadeTroncoMinima().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Estabilidade mínima de tronco inválida: " + dto.estabilidadeTroncoMinima());
        }

        Set<Articulacao> exigencias = new HashSet<>();
        for (Long id : dto.exigenciasIds()) {
            Articulacao art = articulacaoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Articulação exigida não encontrada com ID: " + id));
            exigencias.add(art);
        }

        Exercicio exercicio = new Exercicio();
        exercicio.setNome(dto.nome());
        exercicio.setGrupoMuscular(dto.grupoMuscular());
        exercicio.setEstacaoTrabalho(dto.estacaoTrabalho());
        exercicio.setEstabilidadeTroncoMinima(estabilidade);
        exercicio.setExigencias(exigencias);

        Exercicio savedExercicio = exercicioRepository.save(exercicio);

        List<ExercicioAdaptacao> savedAdaptacoes = new ArrayList<>();
        if (dto.adaptacoes() != null && !dto.adaptacoes().isEmpty()) {
            for (ExercicioAdaptacaoDTO adaptDTO : dto.adaptacoes()) {
                Articulacao art = articulacaoRepository.findById(adaptDTO.articulacaoId())
                        .orElseThrow(() -> new IllegalArgumentException("Articulação de adaptação não encontrada com ID: " + adaptDTO.articulacaoId()));

                Acessorio acessorio = acessorioRepository.findById(adaptDTO.acessorioId())
                        .orElseThrow(() -> new IllegalArgumentException("Acessório não encontrado com ID: " + adaptDTO.acessorioId()));

                ExercicioAdaptacao adaptacao = new ExercicioAdaptacao();
                adaptacao.setExercicio(savedExercicio);
                adaptacao.setArticulacao(art);
                adaptacao.setAcessorio(acessorio);
                adaptacao.setInstrucaoTexto(adaptDTO.instrucaoTexto());

                savedAdaptacoes.add(adaptacaoRepository.save(adaptacao));
            }
        }

        return mapToResponseDTO(savedExercicio, savedAdaptacoes);
    }

    @Transactional(readOnly = true)
    public List<ExercicioResponseDTO> listarExercicios() {
        return exercicioRepository.findAll().stream()
                .map(ex -> {
                    List<ExercicioAdaptacao> adaps = adaptacaoRepository.findByExercicioId(ex.getId());
                    return mapToResponseDTO(ex, adaps);
                })
                .collect(Collectors.toList());
    }

    private ExercicioResponseDTO mapToResponseDTO(Exercicio exercicio, List<ExercicioAdaptacao> adaptacoes) {
        List<String> exigencias = exercicio.getExigencias().stream()
                .map(Articulacao::getNome)
                .collect(Collectors.toList());

        List<ExercicioResponseDTO.AdaptacaoDetail> adaptDetails = adaptacoes.stream()
                .map(ad -> new ExercicioResponseDTO.AdaptacaoDetail(
                        ad.getId(),
                        ad.getArticulacao().getNome(),
                        ad.getAcessorio().getNome(),
                        ad.getInstrucaoTexto()
                ))
                .collect(Collectors.toList());

        return new ExercicioResponseDTO(
                exercicio.getId(),
                exercicio.getNome(),
                exercicio.getGrupoMuscular(),
                exercicio.getEstacaoTrabalho(),
                exercicio.getEstabilidadeTroncoMinima() != null ? exercicio.getEstabilidadeTroncoMinima().name() : null,
                exigencias,
                adaptDetails
        );
    }

    @Transactional
    public Articulacao cadastrarArticulacao(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da articulacao nao pode ser vazio.");
        }
        String nomeUpper = nome.trim().toUpperCase();
        if (articulacaoRepository.existsByNome(nomeUpper)) {
            throw new IllegalArgumentException("Articulacao ja cadastrada: " + nomeUpper);
        }
        return articulacaoRepository.save(new Articulacao(nomeUpper));
    }

    @Transactional(readOnly = true)
    public List<Articulacao> listarArticulacoes() {
        return articulacaoRepository.findAll();
    }

    @Transactional
    public Acessorio cadastrarAcessorio(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do acessorio nao pode ser vazio.");
        }
        String nomeUpper = nome.trim().toUpperCase();
        if (acessorioRepository.existsByNome(nomeUpper)) {
            throw new IllegalArgumentException("Acessorio ja cadastrado: " + nomeUpper);
        }
        return acessorioRepository.save(new Acessorio(nomeUpper));
    }

    @Transactional(readOnly = true)
    public List<Acessorio> listarAcessorios() {
        return acessorioRepository.findAll();
    }
}
