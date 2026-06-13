package com.example.auth.matricula;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import com.example.auth.matricula.AlunoPerfilResponseDTO;
import com.example.auth.matricula.MatriculaDTO;
import com.example.auth.matricula.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
