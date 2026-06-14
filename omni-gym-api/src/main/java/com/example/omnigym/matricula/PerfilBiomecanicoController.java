package com.example.omnigym.matricula;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

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
            @Valid @RequestBody PerfilBiomecanicoDTO dto) {
        AlunoPerfilResponseDTO response = perfilBiomecanicoService.salvarPerfilBiomecanico(alunoId, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instrutor/alunos/{alunoId}/perfil-biomecanico/historico")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<List<PerfilBiomecanicoHistoricoDTO>> obterHistoricoPerfil(
            @PathVariable Long alunoId) {
        List<PerfilBiomecanicoHistoricoDTO> historico = perfilBiomecanicoService.obterHistoricoPerfil(alunoId);
        return ResponseEntity.ok(historico);
    }
}
