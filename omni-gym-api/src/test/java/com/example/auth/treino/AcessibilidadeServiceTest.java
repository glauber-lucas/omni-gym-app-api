package com.example.auth.treino;

import com.example.auth.exercicio.*;
import com.example.auth.matricula.AlunoPerfil;
import com.example.auth.matricula.EstabilidadeTronco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcessibilidadeServiceTest {

    @Mock
    private ExercicioAdaptacaoRepository adaptacaoRepository;

    @InjectMocks
    private AcessibilidadeService acessibilidadeService;

    @Test
    void shouldClassifyAsLiberadoWhenNoJointCollisionAndTroncoIsSufficient() {
        AlunoPerfil perfil = new AlunoPerfil();
        perfil.setBloqueioMedico(false);
        perfil.setEstabilidadeTronco(EstabilidadeTronco.PLENO);
        perfil.setRestricoes(Collections.emptySet());

        Exercicio exercicio = new Exercicio();
        exercicio.setId(1L);
        exercicio.setNome("Supino Reto");
        exercicio.setEstabilidadeTroncoMinima(EstabilidadeTronco.PARCIAL);
        exercicio.setExigencias(Collections.emptySet());

        List<AcessibilidadeService.ResultadoAcessibilidade> resultados = 
                acessibilidadeService.classificarExercicios(perfil, List.of(exercicio));

        assertEquals(1, resultados.size());
        assertEquals(AcessibilidadeService.StatusAcessibilidade.LIBERADO, resultados.get(0).status());
        assertNull(resultados.get(0).acessorio());
    }

    @Test
    void shouldClassifyAsLiberadoComAdaptacaoWhenCollisionExistsAndAdaptationIsMapped() {
        Articulacao ombro = new Articulacao("OMBRO");
        ombro.setId(10L);

        AlunoPerfil perfil = new AlunoPerfil();
        perfil.setBloqueioMedico(false);
        perfil.setEstabilidadeTronco(EstabilidadeTronco.PLENO);
        perfil.setRestricoes(Set.of(ombro));

        Exercicio exercicio = new Exercicio();
        exercicio.setId(1L);
        exercicio.setNome("Supino Reto");
        exercicio.setEstabilidadeTroncoMinima(EstabilidadeTronco.PARCIAL);
        exercicio.setExigencias(Set.of(ombro));

        Acessorio strap = new Acessorio("STRAP");
        ExercicioAdaptacao adaptacao = new ExercicioAdaptacao(exercicio, ombro, strap, "Usar strap ajustado nos punhos.");

        when(adaptacaoRepository.findByExercicioIdAndArticulacaoId(1L, 10L))
                .thenReturn(List.of(adaptacao));

        List<AcessibilidadeService.ResultadoAcessibilidade> resultados = 
                acessibilidadeService.classificarExercicios(perfil, List.of(exercicio));

        assertEquals(1, resultados.size());
        assertEquals(AcessibilidadeService.StatusAcessibilidade.LIBERADO_COM_ADAPTACAO, resultados.get(0).status());
        assertEquals(strap, resultados.get(0).acessorio());
        assertEquals("Usar strap ajustado nos punhos.", resultados.get(0).instrucaoTexto());
    }

    @Test
    void shouldClassifyAsBloqueadoWhenCollisionExistsAndNoAdaptationIsMapped() {
        Articulacao ombro = new Articulacao("OMBRO");
        ombro.setId(10L);

        AlunoPerfil perfil = new AlunoPerfil();
        perfil.setBloqueioMedico(false);
        perfil.setEstabilidadeTronco(EstabilidadeTronco.PLENO);
        perfil.setRestricoes(Set.of(ombro));

        Exercicio exercicio = new Exercicio();
        exercicio.setId(1L);
        exercicio.setNome("Supino Reto");
        exercicio.setEstabilidadeTroncoMinima(EstabilidadeTronco.PARCIAL);
        exercicio.setExigencias(Set.of(ombro));

        when(adaptacaoRepository.findByExercicioIdAndArticulacaoId(1L, 10L))
                .thenReturn(Collections.emptyList());

        List<AcessibilidadeService.ResultadoAcessibilidade> resultados = 
                acessibilidadeService.classificarExercicios(perfil, List.of(exercicio));

        assertEquals(1, resultados.size());
        assertEquals(AcessibilidadeService.StatusAcessibilidade.BLOQUEADO, resultados.get(0).status());
    }

    @Test
    void shouldClassifyAsBloqueadoWhenTroncoIsInsufficient() {
        AlunoPerfil perfil = new AlunoPerfil();
        perfil.setBloqueioMedico(false);
        perfil.setEstabilidadeTronco(EstabilidadeTronco.LIMITADO);
        perfil.setRestricoes(Collections.emptySet());

        Exercicio exercicio = new Exercicio();
        exercicio.setId(1L);
        exercicio.setNome("Supino Reto");
        exercicio.setEstabilidadeTroncoMinima(EstabilidadeTronco.PLENO);
        exercicio.setExigencias(Collections.emptySet());

        List<AcessibilidadeService.ResultadoAcessibilidade> resultados = 
                acessibilidadeService.classificarExercicios(perfil, List.of(exercicio));

        assertEquals(1, resultados.size());
        assertEquals(AcessibilidadeService.StatusAcessibilidade.BLOQUEADO, resultados.get(0).status());
    }

    @Test
    void shouldThrowExceptionWhenBloqueioMedicoIsTrue() {
        AlunoPerfil perfil = new AlunoPerfil();
        perfil.setBloqueioMedico(true);

        Exercicio exercicio = new Exercicio();

        assertThrows(IllegalStateException.class, () -> {
            acessibilidadeService.classificarExercicios(perfil, List.of(exercicio));
        });
    }
}
