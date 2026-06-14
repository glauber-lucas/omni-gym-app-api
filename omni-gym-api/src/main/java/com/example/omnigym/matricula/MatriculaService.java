package com.example.omnigym.matricula;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.omnigym.financeiro.AssinaturaService;
import com.example.omnigym.financeiro.AssinaturaResponseDTO;
import com.example.omnigym.user.User;
import com.example.omnigym.user.UserRepository;

@Service
public class MatriculaService {

    private final AlunoPerfilRepository perfilRepository;
    private final UserRepository userRepository;
    private final AssinaturaService assinaturaService;

    public MatriculaService(AlunoPerfilRepository perfilRepository, UserRepository userRepository, AssinaturaService assinaturaService) {
        this.perfilRepository = perfilRepository;
        this.userRepository = userRepository;
        this.assinaturaService = assinaturaService;
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

        if (perfil.getStatusMatricula() == StatusMatricula.HOMOLOGADA) {
            throw new IllegalStateException("Esta matrícula já foi homologada. Não é possível homologar novamente.");
        }

        validarDadosObrigatorios(perfil);

        perfil.setStatusMatricula(StatusMatricula.HOMOLOGADA);
        AlunoPerfil saved = perfilRepository.save(perfil);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public AlunoPerfilResponseDTO homologarMatriculaComPlano(Long alunoId, Long planoId) {
        AlunoPerfil perfil = perfilRepository.findByUserId(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil do aluno não encontrado para o ID: " + alunoId));

        // Validação 1: Verificar se já foi homologada (impedindo duplicação)
        if (perfil.getStatusMatricula() == StatusMatricula.HOMOLOGADA) {
            throw new IllegalStateException("Esta matrícula já foi homologada. Não é possível homologar novamente.");
        }

        // Validação 2: Verificar se status é AGUARDANDO_HOMOLOGACAO
        if (perfil.getStatusMatricula() != StatusMatricula.AGUARDANDO_HOMOLOGACAO) {
            throw new IllegalStateException("Matrícula não está aguardando homologação");
        }

        // Validação 3: Verificar se todos os dados obrigatórios foram preenchidos
        validarDadosObrigatorios(perfil);

        assinaturaService.criarAssinaturaComFaturas(alunoId, planoId);

        perfil.setStatusMatricula(StatusMatricula.HOMOLOGADA);
        AlunoPerfil saved = perfilRepository.save(perfil);
        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<AlunoPerfilResponseDTO> listarTodas() {
        return perfilRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlunoPerfilResponseDTO obterPerfilPorAlunoId(Long alunoId) {
        AlunoPerfil perfil = perfilRepository.findByUserId(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil do aluno não encontrado para o ID: " + alunoId));
        return mapToResponseDTO(perfil);
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

    /**
     * Valida se todos os dados obrigatórios da matrícula foram preenchidos
     * Campos obrigatórios: telefone, endereco, contatoEmergencia
     * 
     * @param perfil AlunoPerfil a ser validado
     * @throws IllegalArgumentException se algum campo obrigatório estiver vazio ou nulo
     */
    private void validarDadosObrigatorios(AlunoPerfil perfil) {
        StringBuilder erros = new StringBuilder();

        if (perfil.getTelefone() == null || perfil.getTelefone().isBlank()) {
            erros.append("- Telefone é obrigatório\n");
        }

        if (perfil.getEndereco() == null || perfil.getEndereco().isBlank()) {
            erros.append("- Endereço é obrigatório\n");
        }

        if (perfil.getContatoEmergencia() == null || perfil.getContatoEmergencia().isBlank()) {
            erros.append("- Contato de emergência é obrigatório\n");
        }

        if (erros.length() > 0) {
            throw new IllegalArgumentException("Não é possível homologar a matrícula. Dados obrigatórios faltando:\n" + erros.toString());
        }
    }
}
