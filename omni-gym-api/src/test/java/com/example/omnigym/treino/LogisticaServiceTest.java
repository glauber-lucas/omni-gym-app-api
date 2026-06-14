package com.example.omnigym.treino;

import com.example.omnigym.exercicio.Exercicio;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogisticaServiceTest {

    private final LogisticaService logisticaService = new LogisticaService();

    @Test
    void shouldGroupExercisesByWorkstationConsecutively() {
        Exercicio ex1 = new Exercicio();
        ex1.setNome("Triceps Corda");
        ex1.setEstacaoTrabalho("Polia");

        Exercicio ex2 = new Exercicio();
        ex2.setNome("Rosca Direta H");
        ex2.setEstacaoTrabalho("Halteres");

        Exercicio ex3 = new Exercicio();
        ex3.setNome("Puxada Frente");
        ex3.setEstacaoTrabalho("Polia");

        Exercicio ex4 = new Exercicio();
        ex4.setNome("Rosca Concentrada");
        ex4.setEstacaoTrabalho("Halteres");

        AcessibilidadeService.ResultadoAcessibilidade res1 = 
                new AcessibilidadeService.ResultadoAcessibilidade(ex1, AcessibilidadeService.StatusAcessibilidade.LIBERADO, null, null);
        AcessibilidadeService.ResultadoAcessibilidade res2 = 
                new AcessibilidadeService.ResultadoAcessibilidade(ex2, AcessibilidadeService.StatusAcessibilidade.LIBERADO, null, null);
        AcessibilidadeService.ResultadoAcessibilidade res3 = 
                new AcessibilidadeService.ResultadoAcessibilidade(ex3, AcessibilidadeService.StatusAcessibilidade.LIBERADO, null, null);
        AcessibilidadeService.ResultadoAcessibilidade res4 = 
                new AcessibilidadeService.ResultadoAcessibilidade(ex4, AcessibilidadeService.StatusAcessibilidade.LIBERADO, null, null);

        List<AcessibilidadeService.ResultadoAcessibilidade> input = List.of(res1, res2, res3, res4);

        List<AcessibilidadeService.ResultadoAcessibilidade> output = logisticaService.otimizarOrdem(input);

        assertEquals(4, output.size());
        
        assertEquals("Triceps Corda", output.get(0).exercicio().getNome());
        assertEquals("Puxada Frente", output.get(1).exercicio().getNome());
        assertEquals("Rosca Direta H", output.get(2).exercicio().getNome());
        assertEquals("Rosca Concentrada", output.get(3).exercicio().getNome());
    }
}
