package com.example.omnigym.treino;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class FichaTreinoController {

    private final FichaTreinoService fichaTreinoService;

    public FichaTreinoController(FichaTreinoService fichaTreinoService) {
        this.fichaTreinoService = fichaTreinoService;
    }

    @PostMapping("/instrutor/alunos/{alunoId}/treinos")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<FichaTreinoResponseDTO> cadastrarFichaTreino(
            @PathVariable Long alunoId,
            @Valid @RequestBody FichaTreinoDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String instrutorUsername = auth.getName();
        FichaTreinoResponseDTO response = fichaTreinoService.cadastrarFichaTreino(alunoId, instrutorUsername, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/aluno/treino-diario")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<FichaTreinoResponseDTO> obterTreinoDiario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String alunoUsername = auth.getName();
        FichaTreinoResponseDTO response = fichaTreinoService.obterTreinoDiario(alunoUsername);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/aluno/treino/exercicios-disponiveis")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<java.util.List<ExercicioAcessibilidadeResponseDTO>> listarExerciciosDisponiveis() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String alunoUsername = auth.getName();
        java.util.List<ExercicioAcessibilidadeResponseDTO> response = fichaTreinoService.listarExerciciosDisponiveisParaAluno(alunoUsername);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/aluno/treino/editar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<FichaTreinoResponseDTO> editarTreino(@Valid @RequestBody FichaTreinoDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String alunoUsername = auth.getName();
        FichaTreinoResponseDTO response = fichaTreinoService.editarTreinoAluno(alunoUsername, dto);
        return ResponseEntity.ok(response);
    }
}
