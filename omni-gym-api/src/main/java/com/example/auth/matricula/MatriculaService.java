package com.example.auth.matricula;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import com.example.auth.matricula.AlunoPerfilResponseDTO;
import com.example.auth.matricula.MatriculaDTO;
import com.example.auth.matricula.AlunoPerfil;
import com.example.auth.matricula.StatusMatricula;
import com.example.auth.user.User;
import com.example.auth.matricula.AlunoPerfilRepository;
import com.example.auth.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatriculaService {

    private final AlunoPerfilRepository perfilRepository;
    private final UserRepository userRepository;

    public MatriculaService(AlunoPerfilRepository perfilRepository, UserRepository userRepository) {
        this.perfilRepository = perfilRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AlunoPerfilResponseDTO preencherMatricula(String username, MatriculaDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + username));

        AlunoPerfil perfil = perfilRepository.findByUserId(user.getId())
                .orElseGet(() -> new AlunoPerfil(user));

        perfil.setTelefone(dto.telefone());
        perfil.setEndereco(dto.endereco());
        perfil.setContatoEmergencia(dto.contatoEmergencia());
        perfil.setInfoFamiliar(dto.infoFamiliar());
        perfil.setMedicamentos(dto.medicamentos());
        perfil.setDeficiencias(dto.deficiencias());
        perfil.setAlergias(dto.alergias());
        perfil.setStatusMatricula(StatusMatricula.AGUARDANDO_HOMOLOGACAO);

        AlunoPerfil saved = perfilRepository.save(perfil);
        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public AlunoPerfilResponseDTO obterMatricula(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + username));

        AlunoPerfil perfil = perfilRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não preenchida para o usuário: " + username));

        return mapToResponseDTO(perfil);
    }

    @Transactional(readOnly = true)
    public List<AlunoPerfilResponseDTO> listarPendentes() {
        return perfilRepository.findByStatusMatricula(StatusMatricula.AGUARDANDO_HOMOLOGACAO)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlunoPerfilResponseDTO homologarMatricula(Long alunoId) {
        AlunoPerfil perfil = perfilRepository.findByUserId(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil do aluno não encontrado para o ID: " + alunoId));

        perfil.setStatusMatricula(StatusMatricula.HOMOLOGADA);
        AlunoPerfil saved = perfilRepository.save(perfil);
        return mapToResponseDTO(saved);
    }

    private AlunoPerfilResponseDTO mapToResponseDTO(AlunoPerfil perfil) {
        List<String> restricoes = perfil.getRestricoes().stream()
                .map(art -> art.getNome())
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
