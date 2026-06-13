package com.example.auth.controller;

import com.example.auth.clinico.*;
import com.example.auth.exercicio.Exercicio;
import com.example.auth.exercicio.ExercicioRepository;
import com.example.auth.treino.*;
import com.example.auth.user.Role;
import com.example.auth.user.User;
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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClinicoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExercicioRepository exercicioRepository;

    @Autowired
    private FichaTreinoRepository fichaTreinoRepository;

    @Autowired
    private TreinoExercicioRepository treinoExercicioRepository;

    @Autowired
    private DossieClinicoRepository dossieClinicoRepository;

    @Autowired
    private ObservacaoPedagogicaRepository observacaoPedagogicaRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private User aluno;
    private User instrutor;
    private String tokenAluno;
    private String tokenInstrutor;

    private TreinoExercicio treinoExercicio;

    @BeforeEach
    void setUp() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);

        aluno = new User("aluno_" + randomSuffix + "@example.com", "password", Role.ROLE_ALUNO);
        aluno.setName("Aluno Clinico");
        aluno.setDocumentId("DOC_CL_" + randomSuffix);
        userRepository.save(aluno);

        instrutor = new User("instrutor_" + randomSuffix + "@example.com", "password", Role.ROLE_INSTRUTOR);
        instrutor.setName("Instrutor Clinico");
        instrutor.setDocumentId("DOC_IN_" + randomSuffix);
        userRepository.save(instrutor);

        tokenAluno = "Bearer " + tokenService.generateToken(aluno);
        tokenInstrutor = "Bearer " + tokenService.generateToken(instrutor);

        // Pre-create Exercicio & TreinoExercicio for pedagogic observation test
        Exercicio exercicio = new Exercicio();
        exercicio.setNome("Leg Press");
        exercicio.setGrupoMuscular("Pernas");
        exercicio.setEstacaoTrabalho("Leg Press");
        exercicioRepository.save(exercicio);

        FichaTreino ficha = new FichaTreino(aluno, instrutor, "Treino Clinico");
        ficha.setAtiva(true);
        fichaTreinoRepository.save(ficha);

        treinoExercicio = new TreinoExercicio(ficha, exercicio, 3, 12, "100kg", 60, 1);
        treinoExercicioRepository.save(treinoExercicio);
    }

    @Test
    void shouldRegisterAndListDossieClinicoAsInstructor() throws Exception {
        DossieClinicoDTO dto = new DossieClinicoDTO(
                "http://example.com/exame.pdf",
                "Aluno apresenta leve escoliose.",
                new Date(),
                new Date(System.currentTimeMillis() + 86400000L * 30L)
        );

        // 1. Create
        String createResponse = mockMvc.perform(post("/instrutor/alunos/" + aluno.getId() + "/dossie-clinico")
                        .header("Authorization", tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        DossieClinicoResponseDTO response = objectMapper.readValue(createResponse, DossieClinicoResponseDTO.class);
        assertThat(response.laudoMedicoUrl()).isEqualTo("http://example.com/exame.pdf");
        assertThat(response.observacoes()).isEqualTo("Aluno apresenta leve escoliose.");
        assertThat(response.alunoId()).isEqualTo(aluno.getId());

        // 2. List
        String listResponse = mockMvc.perform(get("/instrutor/alunos/" + aluno.getId() + "/dossie-clinico")
                        .header("Authorization", tokenInstrutor))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<?> list = objectMapper.readValue(listResponse, List.class);
        assertThat(list).hasSize(1);
    }

    @Test
    void shouldForbiddenDossieClinicoEndpointsForAluno() throws Exception {
        DossieClinicoDTO dto = new DossieClinicoDTO("http://example.com/exame.pdf", "Obs", new Date(), new Date());

        mockMvc.perform(post("/instrutor/alunos/" + aluno.getId() + "/dossie-clinico")
                        .header("Authorization", tokenAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/instrutor/alunos/" + aluno.getId() + "/dossie-clinico")
                        .header("Authorization", tokenAluno))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRegisterAndListObservacoesPedagogicasSuccessfully() throws Exception {
        ObservacaoPedagogicaDTO dto = new ObservacaoPedagogicaDTO("Manter as costas apoiadas no encosto.");

        // 1. Create observation as instructor
        String createResponse = mockMvc.perform(post("/instrutor/treinos/" + treinoExercicio.getId() + "/observacoes")
                        .header("Authorization", tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ObservacaoPedagogicaResponseDTO response = objectMapper.readValue(createResponse, ObservacaoPedagogicaResponseDTO.class);
        assertThat(response.texto()).isEqualTo("Manter as costas apoiadas no encosto.");
        assertThat(response.treinoExercicioId()).isEqualTo(treinoExercicio.getId());

        // 2. List observations as aluno
        String listResponse = mockMvc.perform(get("/instrutor/treinos/" + treinoExercicio.getId() + "/observacoes")
                        .header("Authorization", tokenAluno))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<?> list = objectMapper.readValue(listResponse, List.class);
        assertThat(list).hasSize(1);
    }

    @Test
    void shouldForbiddenObservacaoRegistrationForAluno() throws Exception {
        ObservacaoPedagogicaDTO dto = new ObservacaoPedagogicaDTO("Texto");

        mockMvc.perform(post("/instrutor/treinos/" + treinoExercicio.getId() + "/observacoes")
                        .header("Authorization", tokenAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
