package com.example.omnigym.exercicio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {
    Optional<Exercicio> findByNome(String nome);
    List<Exercicio> findByGrupoMuscular(String grupoMuscular);
    List<Exercicio> findByEstacaoTrabalho(String estacaoTrabalho);
}
