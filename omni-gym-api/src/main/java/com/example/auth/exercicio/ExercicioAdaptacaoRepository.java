package com.example.auth.exercicio;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import com.example.auth.exercicio.ExercicioAdaptacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercicioAdaptacaoRepository extends JpaRepository<ExercicioAdaptacao, Long> {
    List<ExercicioAdaptacao> findByExercicioId(Long exercicioId);
    List<ExercicioAdaptacao> findByExercicioIdAndArticulacaoId(Long exercicioId, Long articulacaoId);
}
