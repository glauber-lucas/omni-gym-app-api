package com.example.omnigym.controller;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

import com.example.omnigym.matricula.PerfilBiomecanicoDTO;
import com.example.omnigym.core.security.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;
import com.example.omnigym.matricula.AlunoPerfilRepository;
import com.example.omnigym.exercicio.ArticulacaoRepository;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PerfilBiomecanicoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlunoPerfilRepository perfilRepository;

    @Autowired
    private ArticulacaoRepository articulacaoRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private User aluno;
    private User instrutor;
    private String tokenAluno;
    private String tokenInstrutor;
    private Articulacao joelho;
    private Articulacao ombro;

    @BeforeEach
    void setUp() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        aluno = new User("aluno_pb_" + randomSuffix + "@example.com", "password", Role.ROLE_ALUNO);
        aluno.setName("Aluno PB Teste");
        aluno.setDocumentId("doc_pb_" + randomSuffix);
        aluno = userRepository.save(aluno);

        instrutor = new User("instrutor_pb_" + randomSuffix + "@example.com", "password", Role.ROLE_INSTRUTOR);
        instrutor.setName("Instrutor PB Teste");
        instrutor.setDocumentId("doc_pb_ins_" + randomSuffix);
        instrutor = userRepository.save(instrutor);

        tokenAluno = tokenService.generateToken(aluno);
        tokenInstrutor = tokenService.generateToken(instrutor);

        joelho = articulacaoRepository.findByNome("Joelho")
                .orElseGet(() -> articulacaoRepository.save(new Articulacao("Joelho")));
        ombro = articulacaoRepository.findByNome("Ombro")
                .orElseGet(() -> articulacaoRepository.save(new Articulacao("Ombro")));
    }

    @Test
    void alunoNaoPodeDefinirPerfilBiomecanico() throws Exception {
        PerfilBiomecanicoDTO dto = new PerfilBiomecanicoDTO(
                "TOTAL",
                List.of(joelho.getId()),
                false
        );

        mockMvc.perform(post("/instrutor/alunos/" + aluno.getId() + "/perfil-biomecanico")
                        .header("Authorization", "Bearer " + tokenAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void instrutorNaoPodeDefinirPerfilSeMatriculaNaoHomologada() throws Exception {
        AlunoPerfil perfil = new AlunoPerfil(aluno);
        perfil.setTelefone("11988888888");
        perfil.setEndereco("Rua das Oliveiras, 456");
        perfil.setContatoEmergencia("Pai - 11977777777");
        perfil.setStatusMatricula(StatusMatricula.AGUARDANDO_HOMOLOGACAO);
        perfilRepository.save(perfil);

        PerfilBiomecanicoDTO dto = new PerfilBiomecanicoDTO(
                "TOTAL",
                List.of(joelho.getId()),
                false
        );

        mockMvc.perform(post("/instrutor/alunos/" + aluno.getId() + "/perfil-biomecanico")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void instrutorPodeDefinirPerfilSeMatriculaHomologada() throws Exception {
        AlunoPerfil perfil = new AlunoPerfil(aluno);
        perfil.setTelefone("11988888888");
        perfil.setEndereco("Rua das Oliveiras, 456");
        perfil.setContatoEmergencia("Pai - 11977777777");
        perfil.setStatusMatricula(StatusMatricula.HOMOLOGADA);
        perfilRepository.save(perfil);

        PerfilBiomecanicoDTO dto = new PerfilBiomecanicoDTO(
                "PARCIAL",
                List.of(joelho.getId(), ombro.getId()),
                true
        );

        mockMvc.perform(post("/instrutor/alunos/" + aluno.getId() + "/perfil-biomecanico")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        AlunoPerfil perfilAtualizado = perfilRepository.findByUserId(aluno.getId()).orElseThrow();
        assertThat(perfilAtualizado.getEstabilidadeTronco()).isEqualTo(EstabilidadeTronco.PARCIAL);
        assertThat(perfilAtualizado.getBloqueioMedico()).isTrue();
        assertThat(perfilAtualizado.getRestricoes()).containsExactlyInAnyOrder(joelho, ombro);
    }
}
