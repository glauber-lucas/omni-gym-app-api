package com.example.auth.exercicio;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import com.example.auth.exercicio.ExercicioDTO;
import com.example.auth.exercicio.ExercicioResponseDTO;
import com.example.auth.exercicio.ExercicioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
