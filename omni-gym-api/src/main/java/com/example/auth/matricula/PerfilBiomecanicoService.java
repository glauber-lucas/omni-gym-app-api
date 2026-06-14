package com.example.auth.matricula;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auth.exercicio.Articulacao;
import com.example.auth.exercicio.ArticulacaoRepository;

@Service
public class PerfilBiomecanicoService {

    private final AlunoPerfilRepository perfilRepository;
    private final ArticulacaoRepository articulacaoRepository;

    public PerfilBiomecanicoService(AlunoPerfilRepository perfilRepository,
                                    ArticulacaoRepository articulacaoRepository) {
        this.perfilRepository = perfilRepository;
        this.articulacaoRepository = articulacaoRepository;
    }

    @Transactional
    public AlunoPerfilResponseDTO salvarPerfilBiomecanico(Long alunoId, PerfilBiomecanicoDTO dto) {
        AlunoPerfil perfil = perfilRepository.findByUserId(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil do aluno não encontrado para o ID: " + alunoId));

        if (perfil.getStatusMatricula() != StatusMatricula.HOMOLOGADA) {
            throw new IllegalStateException("O perfil do aluno deve estar HOMOLOGADO para definir o perfil biomecânico.");
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
