package com.example.omnigym.clinico;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.omnigym.user.Role;
import com.example.omnigym.user.User;
import com.example.omnigym.user.UserRepository;

@Service
public class DossieClinicoService {

    private final DossieClinicoRepository dossieClinicoRepository;
    private final UserRepository userRepository;

    public DossieClinicoService(DossieClinicoRepository dossieClinicoRepository, UserRepository userRepository) {
        this.dossieClinicoRepository = dossieClinicoRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DossieClinicoResponseDTO cadastrarDossieClinico(Long alunoId, DossieClinicoDTO dto) {
        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado com ID: " + alunoId));

        if (aluno.getRole() != Role.ROLE_ALUNO) {
            throw new IllegalArgumentException("O usuário de destino deve possuir o perfil ROLE_ALUNO.");
        }

        DossieClinico dossie = new DossieClinico(
                aluno,
                dto.laudoMedicoUrl(),
                dto.observacoes(),
                dto.dataAvaliacao(),
                dto.dataProximaReavaliacao()
        );

        DossieClinico saved = dossieClinicoRepository.save(dossie);
        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<DossieClinicoResponseDTO> listarPorAluno(Long alunoId) {
        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado com ID: " + alunoId));

        if (aluno.getRole() != Role.ROLE_ALUNO) {
            throw new IllegalArgumentException("O usuário consultado deve possuir o perfil ROLE_ALUNO.");
        }

        return dossieClinicoRepository.findByAlunoId(alunoId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private DossieClinicoResponseDTO mapToResponseDTO(DossieClinico dossie) {
        return new DossieClinicoResponseDTO(
                dossie.getId(),
                dossie.getAluno().getId(),
                dossie.getLaudoMedicoUrl(),
                dossie.getObservacoes(),
                dossie.getDataAvaliacao(),
                dossie.getDataProximaReavaliacao()
        );
    }
}
