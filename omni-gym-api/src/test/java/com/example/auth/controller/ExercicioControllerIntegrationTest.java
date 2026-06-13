package com.example.auth.controller;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import com.example.auth.exercicio.ExercicioAdaptacaoDTO;
import com.example.auth.exercicio.ExercicioDTO;
import com.example.auth.core.security.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;
import com.example.auth.exercicio.AcessorioRepository;
import com.example.auth.exercicio.ArticulacaoRepository;
import com.example.auth.user.UserRepository;
import com.example.auth.core.security.TokenService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ExercicioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticulacaoRepository articulacaoRepository;

    @Autowired
    private AcessorioRepository acessorioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private User aluno;
    private User instrutor;
    private String tokenAluno;
    private String tokenInstrutor;
    private Articulacao joelho;
    private Articulacao cotovelo;
    private Acessorio strap;

    @BeforeEach
    void setUp() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        aluno = new User("aluno_ex_" + randomSuffix + "@example.com", "password", Role.ROLE_ALUNO);
        aluno.setName("Aluno Ex Teste");
        aluno.setDocumentId("doc_ex_" + randomSuffix);
        aluno = userRepository.save(aluno);

        instrutor = new User("instrutor_ex_" + randomSuffix + "@example.com", "password", Role.ROLE_INSTRUTOR);
        instrutor.setName("Instrutor Ex Teste");
        instrutor.setDocumentId("doc_ex_ins_" + randomSuffix);
        instrutor = userRepository.save(instrutor);

        tokenAluno = tokenService.generateToken(aluno);
        tokenInstrutor = tokenService.generateToken(instrutor);

        joelho = articulacaoRepository.findByNome("Joelho")
                .orElseGet(() -> articulacaoRepository.save(new Articulacao("Joelho")));
        cotovelo = articulacaoRepository.findByNome("Cotovelo")
                .orElseGet(() -> articulacaoRepository.save(new Articulacao("Cotovelo")));
        strap = acessorioRepository.findByNome("Strap")
                .orElseGet(() -> acessorioRepository.save(new Acessorio("Strap")));
    }

    @Test
    void cadastrarExercicioComSucessoSemAdaptacao() throws Exception {
        ExercicioDTO dto = new ExercicioDTO(
                "Agachamento Livre",
                "Pernas",
                "Gaiola de Agachamento",
                "PLENO",
                List.of(joelho.getId()),
                null
        );

        mockMvc.perform(post("/exercicios")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void cadastrarExercicioComSucessoComAdaptacao() throws Exception {
        ExercicioAdaptacaoDTO adaptDTO = new ExercicioAdaptacaoDTO(
                cotovelo.getId(),
                strap.getId(),
                "Usar strap para auxiliar na pegada"
        );

        ExercicioDTO dto = new ExercicioDTO(
                "Rosca Direta com Barra",
                "Braços",
                "Banco de Rosca",
                "LIMITADO",
                List.of(cotovelo.getId()),
                List.of(adaptDTO)
        );

        mockMvc.perform(post("/exercicios")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void cadastrarExercicioSemExigenciasDeveRetornarBadRequest() throws Exception {
        ExercicioDTO dto = new ExercicioDTO(
                "Exercício Inválido",
                "Cardio",
                "Esteira",
                "PLENO",
                List.of(), // Lista de exigências vazia
                null
        );

        mockMvc.perform(post("/exercicios")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void alunoNaoPodeCadastrarExercicio() throws Exception {
        ExercicioDTO dto = new ExercicioDTO(
                "Exercício Proibido",
                "Costas",
                "Polia Alta",
                "PLENO",
                List.of(joelho.getId()),
                null
        );

        mockMvc.perform(post("/exercicios")
                        .header("Authorization", "Bearer " + tokenAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void ambosPodemListarExercicios() throws Exception {
        mockMvc.perform(get("/exercicios")
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isOk());

        mockMvc.perform(get("/exercicios")
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isOk());
    }
}
