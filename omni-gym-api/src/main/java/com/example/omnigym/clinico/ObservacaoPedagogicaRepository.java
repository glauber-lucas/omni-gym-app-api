package com.example.omnigym.clinico;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservacaoPedagogicaRepository extends JpaRepository<ObservacaoPedagogica, Long> {
    List<ObservacaoPedagogica> findByTreinoExercicioId(Long treinoExercicioId);
}
