package com.example.omnigym.controller;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

import com.example.omnigym.exercicio.ExercicioAdaptacaoDTO;
import com.example.omnigym.exercicio.ExercicioDTO;
import com.example.omnigym.core.security.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;
import com.example.omnigym.exercicio.AcessorioRepository;
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

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    private ExercicioRepository exercicioRepository;

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
                null,
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
                List.of(adaptDTO),
                null
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
                null,
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
                null,
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

    @Test
    void instrutorPodeCadastrarArticulacaoEAcessorio() throws Exception {
        String randomName = UUID.randomUUID().toString().substring(0, 8);
        java.util.Map<String, String> articulacaoBody = java.util.Map.of("nome", "ARTICULACAO_" + randomName);

        mockMvc.perform(post("/articulacoes")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articulacaoBody)))
                .andExpect(status().isCreated());

        java.util.Map<String, String> acessorioBody = java.util.Map.of("nome", "ACESSORIO_" + randomName);

        mockMvc.perform(post("/acessorios")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(acessorioBody)))
                .andExpect(status().isCreated());
    }

    @Test
    void alunoNaoPodeCadastrarArticulacaoEAcessorio() throws Exception {
        java.util.Map<String, String> articulacaoBody = java.util.Map.of("nome", "PROIBIDA");

        mockMvc.perform(post("/articulacoes")
                        .header("Authorization", "Bearer " + tokenAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articulacaoBody)))
                .andExpect(status().isForbidden());

        java.util.Map<String, String> acessorioBody = java.util.Map.of("nome", "PROIBIDO");

        mockMvc.perform(post("/acessorios")
                        .header("Authorization", "Bearer " + tokenAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(acessorioBody)))
                .andExpect(status().isForbidden());
    }

    @Test
    void ambosPodemListarArticulacoesEAcessorios() throws Exception {
        mockMvc.perform(get("/articulacoes")
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isOk());

        mockMvc.perform(get("/articulacoes")
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isOk());

        mockMvc.perform(get("/acessorios")
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isOk());

        mockMvc.perform(get("/acessorios")
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isOk());
    }

    @Test
    void instrutorPodeUparImagemParaExercicioEAmbosPodemObter() throws Exception {
        Exercicio exercicio = new Exercicio(
                "Leg Press 45",
                "Pernas",
                "Leg Press",
                EstabilidadeTronco.LIMITADO
        );
        exercicio.getExigencias().add(joelho);
        exercicio = exercicioRepository.save(exercicio);

        MockMultipartFile file = new MockMultipartFile(
                "imagem",
                "legpress.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "leg press image dummy bytes".getBytes()
        );

        // Aluno não pode upar
        mockMvc.perform(MockMvcRequestBuilders.multipart("/exercicios/{id}/imagem", exercicio.getId())
                        .file(file)
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isForbidden());

        // Instrutor pode upar
        String responseContent = mockMvc.perform(MockMvcRequestBuilders.multipart("/exercicios/{id}/imagem", exercicio.getId())
                        .file(file)
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(responseContent).contains("/exercicios/" + exercicio.getId() + "/imagem");
        assertThat(responseContent).contains("imagemUrl");

        // Ambos podem baixar/visualizar a imagem
        mockMvc.perform(get("/exercicios/{id}/imagem", exercicio.getId())
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isOk());

        mockMvc.perform(get("/exercicios/{id}/imagem", exercicio.getId())
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isOk());
    }

    @Test
    void cadastrarExercicioMultipartComImagemComSucesso() throws Exception {
        ExercicioDTO dto = new ExercicioDTO(
                "Agachamento Hack",
                "Pernas",
                "Hack Machine",
                "PLENO",
                List.of(joelho.getId()),
                null,
                null
        );

        MockMultipartFile exercicioPart = new MockMultipartFile(
                "exercicio",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(dto)
        );

        MockMultipartFile filePart = new MockMultipartFile(
                "imagem",
                "hack.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "hack machine image dummy bytes".getBytes()
        );

        String response = mockMvc.perform(MockMvcRequestBuilders.multipart("/exercicios")
                        .file(exercicioPart)
                        .file(filePart)
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Agachamento Hack");
        assertThat(response).contains("/exercicios/");
        assertThat(response).contains("/imagem");
        assertThat(response).contains("imagemUrl");
    }

    @Test
    void cadastrarExercicioMultipartSemImagemComSucesso() throws Exception {
        ExercicioDTO dto = new ExercicioDTO(
                "Flexao de Braco",
                "Peito",
                "Solo",
                "LIMITADO",
                List.of(cotovelo.getId()),
                null,
                null
        );

        MockMultipartFile exercicioPart = new MockMultipartFile(
                "exercicio",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(dto)
        );

        String response = mockMvc.perform(MockMvcRequestBuilders.multipart("/exercicios")
                        .file(exercicioPart)
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Flexao de Braco");
        assertThat(response).doesNotContain("/imagem");
    }

    @Test
    void alunoNaoPodeCadastrarExercicioMultipart() throws Exception {
        ExercicioDTO dto = new ExercicioDTO(
                "Exercicio Invalido",
                "Peito",
                "Solo",
                "LIMITADO",
                List.of(cotovelo.getId()),
                null,
                null
        );

        MockMultipartFile exercicioPart = new MockMultipartFile(
                "exercicio",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(dto)
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/exercicios")
                        .file(exercicioPart)
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isForbidden());
    }
}
