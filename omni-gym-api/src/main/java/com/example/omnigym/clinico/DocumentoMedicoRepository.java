package com.example.omnigym.clinico;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoMedicoRepository extends JpaRepository<DocumentoMedico, Long> {
    List<DocumentoMedico> findByAlunoIdAndAtivoTrue(Long alunoId);
    List<DocumentoMedico> findByAlunoId(Long alunoId);
    Optional<DocumentoMedico> findByIdAndAtivoTrue(Long documentoId);
    List<DocumentoMedico> findByAlunoIdAndTipoOrderByDataUploadDesc(Long alunoId, TipoDocumentoMedico tipo);
}
