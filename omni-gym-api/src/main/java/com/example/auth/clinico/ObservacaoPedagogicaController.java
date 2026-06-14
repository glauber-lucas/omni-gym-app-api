package com.example.auth.clinico;

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
public class ObservacaoPedagogicaController {

    private final ObservacaoPedagogicaService observacaoPedagogicaService;

    public ObservacaoPedagogicaController(ObservacaoPedagogicaService observacaoPedagogicaService) {
        this.observacaoPedagogicaService = observacaoPedagogicaService;
    }

    @PostMapping("/instrutor/treinos/{treinoExercicioId}/observacoes")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<ObservacaoPedagogicaResponseDTO> cadastrarObservacao(
            @PathVariable Long treinoExercicioId,
            @Valid @RequestBody ObservacaoPedagogicaDTO dto) {
        ObservacaoPedagogicaResponseDTO response = observacaoPedagogicaService.cadastrarObservacao(treinoExercicioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/instrutor/treinos/{treinoExercicioId}/observacoes")
    @PreAuthorize("hasAnyRole('INSTRUTOR', 'ALUNO')")
    public ResponseEntity<List<ObservacaoPedagogicaResponseDTO>> listarObservacoes(@PathVariable Long treinoExercicioId) {
        List<ObservacaoPedagogicaResponseDTO> response = observacaoPedagogicaService.listarPorTreinoExercicio(treinoExercicioId);
        return ResponseEntity.ok(response);
    }
}
