package com.example.auth.exercicio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExercicioAdaptacaoRepository extends JpaRepository<ExercicioAdaptacao, Long> {
    List<ExercicioAdaptacao> findByExercicioId(Long exercicioId);
    List<ExercicioAdaptacao> findByExercicioIdAndArticulacaoId(Long exercicioId, Long articulacaoId);
}
