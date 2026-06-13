package com.example.auth.matricula;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import com.example.auth.matricula.AlunoPerfilResponseDTO;
import com.example.auth.matricula.BiomechanicalProfileDTO;
import com.example.auth.matricula.PerfilBiomecanicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PerfilBiomecanicoController {

    private final PerfilBiomecanicoService perfilBiomecanicoService;

    public PerfilBiomecanicoController(PerfilBiomecanicoService perfilBiomecanicoService) {
        this.perfilBiomecanicoService = perfilBiomecanicoService;
    }

    @PostMapping("/instrutor/alunos/{alunoId}/perfil-biomecanico")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<AlunoPerfilResponseDTO> salvarPerfilBiomecanico(
            @PathVariable Long alunoId,
            @Valid @RequestBody BiomechanicalProfileDTO dto) {
        AlunoPerfilResponseDTO response = perfilBiomecanicoService.salvarPerfilBiomecanico(alunoId, dto);
        return ResponseEntity.ok(response);
    }
}
