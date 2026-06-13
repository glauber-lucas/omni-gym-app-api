package com.example.auth.exercicio;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import com.example.auth.exercicio.Exercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {
    Optional<Exercicio> findByNome(String nome);
    List<Exercicio> findByGrupoMuscular(String grupoMuscular);
    List<Exercicio> findByEstacaoTrabalho(String estacaoTrabalho);
}
