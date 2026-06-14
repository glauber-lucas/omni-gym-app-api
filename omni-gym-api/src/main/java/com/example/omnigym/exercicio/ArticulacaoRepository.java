package com.example.omnigym.exercicio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticulacaoRepository extends JpaRepository<Articulacao, Long> {
    Optional<Articulacao> findByNome(String nome);
    boolean existsByNome(String nome);
}
