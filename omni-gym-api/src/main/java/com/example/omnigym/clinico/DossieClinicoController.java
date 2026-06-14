package com.example.omnigym.clinico;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class DossieClinicoController {

    private final DossieClinicoService dossieClinicoService;

    public DossieClinicoController(DossieClinicoService dossieClinicoService) {
        this.dossieClinicoService = dossieClinicoService;
    }

    @PostMapping("/instrutor/alunos/{alunoId}/dossie-clinico")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<DossieClinicoResponseDTO> cadastrarDossieClinico(
            @PathVariable Long alunoId,
            @Valid @RequestBody DossieClinicoDTO dto) {
        DossieClinicoResponseDTO response = dossieClinicoService.cadastrarDossieClinico(alunoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/instrutor/alunos/{alunoId}/dossie-clinico")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<List<DossieClinicoResponseDTO>> listarDossieClinico(@PathVariable Long alunoId) {
        List<DossieClinicoResponseDTO> response = dossieClinicoService.listarPorAluno(alunoId);
        return ResponseEntity.ok(response);
    }
}
