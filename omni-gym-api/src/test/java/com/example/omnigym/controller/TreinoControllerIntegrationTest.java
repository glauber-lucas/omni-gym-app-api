package com.example.omnigym.controller;

import com.example.omnigym.exercicio.*;
import com.example.omnigym.matricula.AlunoPerfil;
import com.example.omnigym.matricula.AlunoPerfilRepository;
import com.example.omnigym.matricula.EstabilidadeTronco;
import com.example.omnigym.matricula.StatusMatricula;
import com.example.omnigym.treino.*;
import com.example.omnigym.user.Role;
import com.example.omnigym.user.User;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TreinoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlunoPerfilRepository perfilRepository;

    @Autowired
    private ExercicioRepository exercicioRepository;

    @Autowired
    private ArticulacaoRepository articulacaoRepository;

    @Autowired
    private AcessorioRepository acessorioRepository;

    @Autowired
    private ExercicioAdaptacaoRepository adaptacaoRepository;

    @Autowired
    private FichaTreinoRepository fichaTreinoRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private User aluno;
    private User instrutor;
    private String tokenAluno;
    private String tokenInstrutor;

    private Articulacao ombro;
    private Acessorio strap;

    private Exercicio exercicioBloqueadoTronco;
    private Exercicio exercicioAdaptado;
    private Exercicio exercicioLiberado1;
    private Exercicio exercicioLiberado2;

    @BeforeEach
    void setUp() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);

        // 1. Create Users
        aluno = new User("aluno_" + randomSuffix + "@example.com", "password", Role.ROLE_ALUNO);
        aluno.setName("Aluno Treino");
        aluno.setDocumentId("DOC_AL_" + randomSuffix);
        userRepository.save(aluno);

        instrutor = new User("instrutor_" + randomSuffix + "@example.com", "password", Role.ROLE_INSTRUTOR);
        instrutor.setName("Instrutor Treino");
        instrutor.setDocumentId("DOC_IN_" + randomSuffix);
        userRepository.save(instrutor);

        tokenAluno = "Bearer " + tokenService.generateToken(aluno);
        tokenInstrutor = "Bearer " + tokenService.generateToken(instrutor);

        ombro = articulacaoRepository.findByNome("OMBRO")
                .orElseGet(() -> articulacaoRepository.save(new Articulacao("OMBRO")));
        strap = acessorioRepository.findByNome("STRAP")
                .orElseGet(() -> acessorioRepository.save(new Acessorio("STRAP")));

        exercicioBloqueadoTronco = new Exercicio();
        exercicioBloqueadoTronco.setNome("Supino Instavel");
        exercicioBloqueadoTronco.setGrupoMuscular("Peito");
        exercicioBloqueadoTronco.setEstacaoTrabalho("Polia");
        exercicioBloqueadoTronco.setEstabilidadeTroncoMinima(EstabilidadeTronco.PLENO);
        exercicioBloqueadoTronco.setExigencias(Set.of(ombro));
        exercicioRepository.save(exercicioBloqueadoTronco);

        exercicioAdaptado = new Exercicio();
        exercicioAdaptado.setNome("Desenvolvimento Ombro");
        exercicioAdaptado.setGrupoMuscular("Ombros");
        exercicioAdaptado.setEstacaoTrabalho("Polia");
        exercicioAdaptado.setEstabilidadeTroncoMinima(EstabilidadeTronco.PARCIAL);
        exercicioAdaptado.setExigencias(Set.of(ombro));
        exercicioRepository.save(exercicioAdaptado);

        ExercicioAdaptacao adaptacao = new ExercicioAdaptacao(exercicioAdaptado, ombro, strap, "Usar alca adaptada.");
        adaptacaoRepository.save(adaptacao);

        exercicioLiberado1 = new Exercicio();
        exercicioLiberado1.setNome("Rosca Biceps");
        exercicioLiberado1.setGrupoMuscular("Biceps");
        exercicioLiberado1.setEstacaoTrabalho("Halteres");
        exercicioLiberado1.setEstabilidadeTroncoMinima(EstabilidadeTronco.LIMITADO);
        exercicioLiberado1.setExigencias(Set.of());
        exercicioRepository.save(exercicioLiberado1);

        exercicioLiberado2 = new Exercicio();
        exercicioLiberado2.setNome("Triceps Corda");
        exercicioLiberado2.setGrupoMuscular("Triceps");
        exercicioLiberado2.setEstacaoTrabalho("Polia");
        exercicioLiberado2.setEstabilidadeTroncoMinima(EstabilidadeTronco.PARCIAL);
        exercicioLiberado2.setExigencias(Set.of());
        exercicioRepository.save(exercicioLiberado2);
    }

    @Test
    void shouldRegisterFichaTreinoAsInstructorSuccessfully() throws Exception {
        AlunoPerfil perfil = new AlunoPerfil(aluno);
        perfil.setStatusMatricula(StatusMatricula.HOMOLOGADA);
        perfilRepository.save(perfil);

        TreinoExercicioDTO item1 = new TreinoExercicioDTO(exercicioAdaptado.getId(), 3, 10, "15kg", 60, 1);
        TreinoExercicioDTO item2 = new TreinoExercicioDTO(exercicioLiberado1.getId(), 4, 12, "10kg", 45, 2);

        FichaTreinoDTO dto = new FichaTreinoDTO("Treino Forca A", List.of(item1, item2));

        String responseContent = mockMvc.perform(post("/instrutor/alunos/" + aluno.getId() + "/treinos")
                        .header("Authorization", tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        FichaTreinoResponseDTO response = objectMapper.readValue(responseContent, FichaTreinoResponseDTO.class);
        assertThat(response.nome()).isEqualTo("Treino Forca A");
        assertThat(response.exercicios()).hasSize(2);
    }

    @Test
    void shouldFailRegisterFichaTreinoAsAluno() throws Exception {
        TreinoExercicioDTO item1 = new TreinoExercicioDTO(exercicioLiberado1.getId(), 3, 10, "10kg", 45, 1);
        FichaTreinoDTO dto = new FichaTreinoDTO("Treino Auto", List.of(item1));

        mockMvc.perform(post("/instrutor/alunos/" + aluno.getId() + "/treinos")
                        .header("Authorization", tokenAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRetrieveDailyTreinoFilteredByAccessibilityAndSortedByStation() throws Exception {
        AlunoPerfil perfil = new AlunoPerfil(aluno);
        perfil.setStatusMatricula(StatusMatricula.HOMOLOGADA);
        perfil.setEstabilidadeTronco(EstabilidadeTronco.PARCIAL);
        perfil.setRestricoes(Set.of(ombro));
        perfilRepository.save(perfil);

        FichaTreino ficha = new FichaTreino(aluno, instrutor, "Treino A");
        ficha.setAtiva(true);
        fichaTreinoRepository.save(ficha);

        TreinoExercicio item1 = new TreinoExercicio(ficha, exercicioBloqueadoTronco, 3, 10, "10kg", 60, 1);
        TreinoExercicio item2 = new TreinoExercicio(ficha, exercicioAdaptado, 3, 10, "10kg", 60, 2);
        TreinoExercicio item3 = new TreinoExercicio(ficha, exercicioLiberado1, 4, 12, "8kg", 45, 3);
        TreinoExercicio item4 = new TreinoExercicio(ficha, exercicioLiberado2, 3, 15, "20kg", 60, 4);

        ficha.setExercicios(new java.util.ArrayList<>(List.of(item1, item2, item3, item4)));
        fichaTreinoRepository.save(ficha);

        String responseContent = mockMvc.perform(get("/aluno/treino-diario")
                        .header("Authorization", tokenAluno))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        FichaTreinoResponseDTO response = objectMapper.readValue(responseContent, FichaTreinoResponseDTO.class);

        assertThat(response.exercicios()).hasSize(3);

        List<String> exerciciosNomes = response.exercicios().stream()
                .map(TreinoExercicioResponseDTO::exercicioNome)
                .toList();

        assertThat(exerciciosNomes).doesNotContain("Supino Instavel");

        TreinoExercicioResponseDTO adaptedDto = response.exercicios().stream()
                .filter(te -> te.exercicioNome().equals("Desenvolvimento Ombro"))
                .findFirst().orElseThrow();
        assertThat(adaptedDto.statusAcessibilidade()).isEqualTo("LIBERADO_COM_ADAPTACAO");
        assertThat(adaptedDto.acessorioNecessario()).isEqualTo("STRAP");
        assertThat(adaptedDto.instrucaoAdaptacao()).isEqualTo("Usar alca adaptada.");

        List<String> stations = response.exercicios().stream()
                .map(TreinoExercicioResponseDTO::estacaoTrabalho)
                .toList();

        if (stations.get(0).equals("Polia")) {
            assertThat(stations.get(1)).isEqualTo("Polia");
            assertThat(stations.get(2)).isEqualTo("Halteres");
        } else {
            assertThat(stations.get(0)).isEqualTo("Halteres");
            assertThat(stations.get(1)).isEqualTo("Polia");
            assertThat(stations.get(2)).isEqualTo("Polia");
        }
    }
}
