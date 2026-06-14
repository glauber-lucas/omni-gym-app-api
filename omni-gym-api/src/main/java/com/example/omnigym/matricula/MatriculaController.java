package com.example.omnigym.matricula;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping("/aluno/matricula")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<AlunoPerfilResponseDTO> preencherMatricula(@Valid @RequestBody MatriculaDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        AlunoPerfilResponseDTO response = matriculaService.preencherMatricula(username, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/aluno/matricula")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<AlunoPerfilResponseDTO> obterMatricula() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        AlunoPerfilResponseDTO response = matriculaService.obterMatricula(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instrutor/matriculas/pendentes")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<List<AlunoPerfilResponseDTO>> listarPendentes() {
        List<AlunoPerfilResponseDTO> response = matriculaService.listarPendentes();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/instrutor/matriculas/{alunoId}/homologar")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<AlunoPerfilResponseDTO> homologarMatricula(@PathVariable Long alunoId) {
        AlunoPerfilResponseDTO response = matriculaService.homologarMatricula(alunoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instrutor/matriculas")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<List<AlunoPerfilResponseDTO>> listarTodas() {
        List<AlunoPerfilResponseDTO> response = matriculaService.listarTodas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instrutor/matriculas/{alunoId}")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<AlunoPerfilResponseDTO> obterPerfilPorAlunoId(@PathVariable Long alunoId) {
        AlunoPerfilResponseDTO response = matriculaService.obterPerfilPorAlunoId(alunoId);
        return ResponseEntity.ok(response);
    }
}
