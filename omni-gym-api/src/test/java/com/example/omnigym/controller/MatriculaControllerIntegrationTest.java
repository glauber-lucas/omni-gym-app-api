package com.example.omnigym.controller;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

import com.example.omnigym.matricula.MatriculaDTO;
import com.example.omnigym.matricula.AlunoPerfil;
import com.example.omnigym.user.Role;
import com.example.omnigym.matricula.StatusMatricula;
import com.example.omnigym.user.User;
import com.example.omnigym.matricula.AlunoPerfilRepository;
import com.example.omnigym.user.UserRepository;
import com.example.omnigym.core.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MatriculaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlunoPerfilRepository perfilRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private User aluno;
    private User instrutor;
    private String tokenAluno;
    private String tokenInstrutor;

    @BeforeEach
    void setUp() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        aluno = new User("aluno_" + randomSuffix + "@example.com", "password", Role.ROLE_ALUNO);
        aluno.setName("Aluno Teste");
        aluno.setDocumentId("doc_" + randomSuffix);
        aluno = userRepository.save(aluno);

        instrutor = new User("instrutor_" + randomSuffix + "@example.com", "password", Role.ROLE_INSTRUTOR);
        instrutor.setName("Instrutor Teste");
        instrutor.setDocumentId("doc_ins_" + randomSuffix);
        instrutor = userRepository.save(instrutor);

        tokenAluno = tokenService.generateToken(aluno);
        tokenInstrutor = tokenService.generateToken(instrutor);
    }

    @Test
    void alunoPodePreencherEConsultarMatricula() throws Exception {
        MatriculaDTO dto = new MatriculaDTO(
                "11999999999",
                "Rua de Teste, 123",
                "Maria - 11988888888",
                "Família Saudável",
                "Nenhum",
                "Nenhuma",
                "Nenhuma"
        );

        // Preenche matricula
        mockMvc.perform(post("/aluno/matricula")
                        .header("Authorization", "Bearer " + tokenAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Consulta matricula
        mockMvc.perform(get("/aluno/matricula")
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isOk());
    }

    @Test
    void alunoNaoPodeAcessarRotasDeInstrutor() throws Exception {
        mockMvc.perform(get("/instrutor/matriculas/pendentes")
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/instrutor/matriculas/" + aluno.getId() + "/homologar")
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isForbidden());
    }

    @Test
    void instrutorPodeListarHomologarMatricula() throws Exception {
        // Aluno preenche
        AlunoPerfil perfil = new AlunoPerfil(aluno);
        perfil.setTelefone("11988888888");
        perfil.setEndereco("Rua das Oliveiras, 456");
        perfil.setContatoEmergencia("Pai - 11977777777");
        perfil.setStatusMatricula(StatusMatricula.AGUARDANDO_HOMOLOGACAO);
        perfilRepository.save(perfil);

        // Instrutor lista pendentes
        mockMvc.perform(get("/instrutor/matriculas/pendentes")
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isOk());

        // Instrutor homologa
        mockMvc.perform(post("/instrutor/matriculas/" + aluno.getId() + "/homologar")
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isOk());

        AlunoPerfil perfilHomologado = perfilRepository.findByUserId(aluno.getId()).orElseThrow();
        assertThat(perfilHomologado.getStatusMatricula()).isEqualTo(StatusMatricula.HOMOLOGADA);
    }

    @Test
    void instrutorPodeListarTodasEVerDetalhesDeMatriculas() throws Exception {
        AlunoPerfil perfil = new AlunoPerfil(aluno);
        perfil.setTelefone("11988888888");
        perfil.setEndereco("Rua das Rosas, 789");
        perfil.setContatoEmergencia("Mãe - 11966666666");
        perfil.setStatusMatricula(StatusMatricula.HOMOLOGADA);
        perfilRepository.save(perfil);

        // Instrutor lista todas
        mockMvc.perform(get("/instrutor/matriculas")
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isOk());

        // Instrutor detalha uma
        mockMvc.perform(get("/instrutor/matriculas/" + aluno.getId())
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isOk());

        // Aluno não pode listar todas
        mockMvc.perform(get("/instrutor/matriculas")
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isForbidden());
    }
}
