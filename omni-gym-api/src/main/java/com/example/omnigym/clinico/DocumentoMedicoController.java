package com.example.omnigym.clinico;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class DocumentoMedicoController {

    private final DocumentoMedicoService documentoService;

    public DocumentoMedicoController(DocumentoMedicoService documentoService) {
        this.documentoService = documentoService;
    }

    /**
     * Upload de documento médico pelo aluno
     * POST /aluno/documentos-medicos/upload
     */
    @PostMapping("/aluno/documentos-medicos/upload")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<DocumentoMedicoResponseDTO> uploadDocumento(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("tipo") String tipo,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "dataProximaReavaliacao", required = false) String dataProximaReavaliacao,
            Authentication auth) throws IOException {

        String username = auth.getName();
        
        // Obter ID do aluno do token/sessão
        Long alunoId = extractAlunoIdFromAuth(auth);

        DocumentoMedicoUploadDTO dto = new DocumentoMedicoUploadDTO(
            tipo,
            descricao,
            dataProximaReavaliacao != null ? java.time.LocalDateTime.parse(dataProximaReavaliacao) : null
        );

        DocumentoMedicoResponseDTO response = documentoService.uploadDocumento(alunoId, username, dto, arquivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar documentos médicos do aluno (instrutor visualiza)
     * GET /instrutor/alunos/{alunoId}/documentos-medicos
     */
    @GetMapping("/instrutor/alunos/{alunoId}/documentos-medicos")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<List<DocumentoMedicoResponseDTO>> listarDocumentos(
            @PathVariable Long alunoId) {
        List<DocumentoMedicoResponseDTO> documentos = documentoService.listarDocumentos(alunoId);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Listar documentos médicos por tipo
     * GET /instrutor/alunos/{alunoId}/documentos-medicos?tipo=LAUDO_MEDICO
     */
    @GetMapping("/instrutor/alunos/{alunoId}/documentos-medicos/tipo")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<List<DocumentoMedicoResponseDTO>> listarDocumentosPorTipo(
            @PathVariable Long alunoId,
            @RequestParam String tipo) {
        List<DocumentoMedicoResponseDTO> documentos = documentoService.listarDocumentosPorTipo(alunoId, tipo);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Download seguro de documento (com auditoria)
     * GET /api/documentos/{documentoId}/download
     */
    @GetMapping("/api/documentos/{documentoId}/download")
    @PreAuthorize("hasRole('INSTRUTOR') or hasRole('ALUNO')")
    public ResponseEntity<Resource> downloadDocumento(
            @PathVariable Long documentoId,
            HttpServletRequest request,
            Authentication auth) {

        String username = auth.getName();
        boolean isInstrutor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUTOR"));

        if (!isInstrutor && !documentoService.isAlunoDono(documentoId, username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String ipAddress = extractIpAddress(request);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);

        return documentoService.downloadDocumento(documentoId, username, ipAddress, userAgent);
    }

    /**
     * Deletar documento (soft delete)
     * DELETE /documentos/{documentoId}
     */
    @DeleteMapping("/documentos/{documentoId}")
    @PreAuthorize("hasRole('ALUNO') or hasRole('INSTRUTOR')")
    public ResponseEntity<Void> deletarDocumento(
            @PathVariable Long documentoId,
            Authentication auth) {

        String username = auth.getName();
        boolean isInstrutor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUTOR"));

        if (!isInstrutor && !documentoService.isAlunoDono(documentoId, username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        documentoService.deletarDocumento(documentoId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obter histórico de acesso ao documento (auditoria)
     * GET /instrutor/documentos/{documentoId}/historico-acesso
     */
    @GetMapping("/instrutor/documentos/{documentoId}/historico-acesso")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<List<DocumentoAcessoAuditoria>> obterHistoricoAcesso(
            @PathVariable Long documentoId) {

        List<DocumentoAcessoAuditoria> historico = documentoService.obterHistoricoAcesso(documentoId);
        return ResponseEntity.ok(historico);
    }

    // ===== Métodos Auxiliares =====

    private Long extractAlunoIdFromAuth(Authentication auth) {
        String username = auth.getName();
        return documentoService.getUserIdByUsername(username);
    }

    private String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
