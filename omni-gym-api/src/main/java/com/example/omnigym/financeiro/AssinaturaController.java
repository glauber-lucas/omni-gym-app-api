package com.example.omnigym.financeiro;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class AssinaturaController {

    private final AssinaturaService assinaturaService;

    public AssinaturaController(AssinaturaService assinaturaService) {
        this.assinaturaService = assinaturaService;
    }

    @PostMapping("/instrutor/financeiro/alunos/{alunoId}/assinatura")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<AssinaturaResponseDTO> criarAssinatura(
            @PathVariable Long alunoId,
            @Valid @RequestBody AssinaturaRequestDTO dto) {

        AssinaturaResponseDTO response = assinaturaService.criarAssinaturaComFaturas(alunoId, dto.planoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/instrutor/financeiro/alunos/{alunoId}/assinatura")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<AssinaturaResponseDTO> obterAssinatura(
            @PathVariable Long alunoId) {

        AssinaturaResponseDTO response = assinaturaService.obterAssinaturaAtiva(alunoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instrutor/financeiro/alunos/{alunoId}/assinaturas")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<List<AssinaturaResponseDTO>> listarAssinaturas(
            @PathVariable Long alunoId) {

        List<AssinaturaResponseDTO> response = assinaturaService.listarAssinaturasDoAluno(alunoId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/instrutor/financeiro/assinatura/{assinaturaId}/cancelar")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<Void> cancelarAssinatura(
            @PathVariable Long assinaturaId) {

        assinaturaService.cancelarAssinatura(assinaturaId);
        return ResponseEntity.noContent().build();
    }
}
