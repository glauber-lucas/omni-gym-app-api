package com.example.omnigym.matricula;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.omnigym.exercicio.Articulacao;
import com.example.omnigym.exercicio.ArticulacaoRepository;

@Service
public class PerfilBiomecanicoService {

    private final AlunoPerfilRepository perfilRepository;
    private final ArticulacaoRepository articulacaoRepository;
    private final PerfilBiomecanicoHistoricoRepository historicoRepository;

    public PerfilBiomecanicoService(AlunoPerfilRepository perfilRepository,
                                    ArticulacaoRepository articulacaoRepository,
                                    PerfilBiomecanicoHistoricoRepository historicoRepository) {
        this.perfilRepository = perfilRepository;
        this.articulacaoRepository = articulacaoRepository;
        this.historicoRepository = historicoRepository;
    }

    @Transactional
    public AlunoPerfilResponseDTO salvarPerfilBiomecanico(Long alunoId, PerfilBiomecanicoDTO dto) {
        AlunoPerfil perfil = perfilRepository.findByUserId(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil do aluno não encontrado para o ID: " + alunoId));

        if (perfil.getStatusMatricula() != StatusMatricula.HOMOLOGADA) {
            throw new IllegalStateException("O perfil do aluno deve estar HOMOLOGADO para definir o perfil biomecânico.");
        }

        // Salvar versão anterior no histórico (se o perfil biomecânico já foi definido)
        if (perfil.getEstabilidadeTronco() != null) {
            salvarHistorico(perfil);
        }

        try {
            EstabilidadeTronco estabilidade = EstabilidadeTronco.valueOf(dto.estabilidadeTronco().toUpperCase());
            perfil.setEstabilidadeTronco(estabilidade);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Estabilidade de tronco inválida: " + dto.estabilidadeTronco());
        }

        if (dto.bloqueioMedico() != null) {
            perfil.setBloqueioMedico(dto.bloqueioMedico());
        }

        Set<Articulacao> restricoes = new HashSet<>();
        if (dto.restricoesIds() != null) {
            for (Long id : dto.restricoesIds()) {
                Articulacao art = articulacaoRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Articulação não encontrada com o ID: " + id));
                restricoes.add(art);
            }
        }
        perfil.setRestricoes(restricoes);

        AlunoPerfil saved = perfilRepository.save(perfil);
        return mapToResponseDTO(saved);
    }

    /**
     * Lista o histórico de mudanças do perfil biomecânico de um aluno
     * 
     * @param alunoId ID do aluno
     * @return Lista de PerfilBiomecanicoHistoricoDTO ordenada por data (mais recente primeiro)
     */
    @Transactional(readOnly = true)
    public List<PerfilBiomecanicoHistoricoDTO> obterHistoricoPerfil(Long alunoId) {
        AlunoPerfil perfil = perfilRepository.findByUserId(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil do aluno não encontrado para o ID: " + alunoId));

        return historicoRepository.findByAlunoPerfilIdOrderByDataCriacaoDesc(perfil.getId())
                .stream()
                .map(this::mapHistoricoToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Salva o perfil biomecânico atual no histórico antes de atualizar
     * 
     * @param perfil AlunoPerfil do qual fazer backup do perfil biomecânico
     */
    private void salvarHistorico(AlunoPerfil perfil) {
        PerfilBiomecanicoHistorico historico = new PerfilBiomecanicoHistorico(
            perfil,
            perfil.getEstabilidadeTronco(),
            perfil.getBloqueioMedico(),
            new HashSet<>(perfil.getRestricoes())
        );
        historicoRepository.save(historico);
    }

    private PerfilBiomecanicoHistoricoDTO mapHistoricoToDTO(PerfilBiomecanicoHistorico historico) {
        List<String> restricoes = historico.getRestricoes().stream()
                .map(Articulacao::getNome)
                .collect(Collectors.toList());

        return new PerfilBiomecanicoHistoricoDTO(
                historico.getId(),
                historico.getEstabilidadeTronco() != null ? historico.getEstabilidadeTronco().name() : null,
                historico.getBloqueioMedico(),
                restricoes,
                historico.getDataCriacao(),
                historico.getDataReavaliacao(),
                historico.getObservacoes()
        );
    }

    private AlunoPerfilResponseDTO mapToResponseDTO(AlunoPerfil perfil) {
        List<String> restricoes = perfil.getRestricoes().stream()
                .map(Articulacao::getNome)
                .collect(Collectors.toList());

        return new AlunoPerfilResponseDTO(
                perfil.getId(),
                perfil.getUser().getId(),
                perfil.getUser().getName(),
                perfil.getUser().getDocumentId(),
                perfil.getUser().getUsername(),
                perfil.getTelefone(),
                perfil.getEndereco(),
                perfil.getContatoEmergencia(),
                perfil.getInfoFamiliar(),
                perfil.getMedicamentos(),
                perfil.getDeficiencias(),
                perfil.getAlergias(),
                perfil.getStatusMatricula() != null ? perfil.getStatusMatricula().name() : null,
                perfil.getEstabilidadeTronco() != null ? perfil.getEstabilidadeTronco().name() : null,
                perfil.getBloqueioMedico(),
                restricoes
        );
    }
}
