package com.example.omnigym.clinico;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoAcessoAuditoriaRepository extends JpaRepository<DocumentoAcessoAuditoria, Long> {
    List<DocumentoAcessoAuditoria> findByDocumentoIdOrderByDataAcessoDesc(Long documentoId);
    List<DocumentoAcessoAuditoria> findByDocumentoIdAndUsuarioIdOrderByDataAcessoDesc(Long documentoId, Long usuarioId);
}
