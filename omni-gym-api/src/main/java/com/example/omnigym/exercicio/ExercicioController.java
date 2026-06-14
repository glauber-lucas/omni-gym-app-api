package com.example.omnigym.exercicio;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class ExercicioController {

    private final ExercicioService exercicioService;

    public ExercicioController(ExercicioService exercicioService) {
        this.exercicioService = exercicioService;
    }

    @PostMapping("/exercicios")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<ExercicioResponseDTO> cadastrarExercicio(@Valid @RequestBody ExercicioDTO dto) {
        ExercicioResponseDTO response = exercicioService.cadastrarExercicio(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/exercicios")
    @PreAuthorize("hasAnyRole('INSTRUTOR', 'ALUNO')")
    public ResponseEntity<List<ExercicioResponseDTO>> listarExercicios() {
        List<ExercicioResponseDTO> response = exercicioService.listarExercicios();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/articulacoes")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<Articulacao> cadastrarArticulacao(@RequestBody java.util.Map<String, String> body) {
        String nome = body.get("nome");
        Articulacao response = exercicioService.cadastrarArticulacao(nome);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/articulacoes")
    @PreAuthorize("hasAnyRole('INSTRUTOR', 'ALUNO')")
    public ResponseEntity<List<Articulacao>> listarArticulacoes() {
        List<Articulacao> response = exercicioService.listarArticulacoes();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/acessorios")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<Acessorio> cadastrarAcessorio(@RequestBody java.util.Map<String, String> body) {
        String nome = body.get("nome");
        Acessorio response = exercicioService.cadastrarAcessorio(nome);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/acessorios")
    @PreAuthorize("hasAnyRole('INSTRUTOR', 'ALUNO')")
    public ResponseEntity<List<Acessorio>> listarAcessorios() {
        List<Acessorio> response = exercicioService.listarAcessorios();
        return ResponseEntity.ok(response);
    }
}
