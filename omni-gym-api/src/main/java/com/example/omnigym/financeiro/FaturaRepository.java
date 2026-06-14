package com.example.omnigym.financeiro;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaturaRepository extends JpaRepository<Fatura, Long> {
    List<Fatura> findByAlunoId(Long alunoId);
    List<Fatura> findByStatus(String status);
    List<Fatura> findByAlunoIdAndPlanoId(Long alunoId, Long planoId);
    List<Fatura> findByAlunoIdAndStatus(Long alunoId, String status);
}
