package com.example.omnigym.matricula;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilBiomecanicoHistoricoRepository extends JpaRepository<PerfilBiomecanicoHistorico, Long> {
    List<PerfilBiomecanicoHistorico> findByAlunoPerfilIdOrderByDataCriacaoDesc(Long alunoId);
    List<PerfilBiomecanicoHistorico> findByAlunoPerfilId(Long alunoId);
}
