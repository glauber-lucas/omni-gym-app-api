package com.example.omnigym.clinico;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DossieClinicoRepository extends JpaRepository<DossieClinico, Long> {
    List<DossieClinico> findByAlunoId(Long alunoId);
}
