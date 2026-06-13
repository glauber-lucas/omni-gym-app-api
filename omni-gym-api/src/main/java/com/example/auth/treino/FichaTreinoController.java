package com.example.auth.treino;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
}
