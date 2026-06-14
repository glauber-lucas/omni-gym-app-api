package com.example.omnigym.financeiro;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {
    Optional<Assinatura> findByAlunoIdAndStatusOrderByDataInicioDesc(Long alunoId, StatusAssinatura status);
    List<Assinatura> findByAlunoId(Long alunoId);
    List<Assinatura> findByStatus(StatusAssinatura status);
}
