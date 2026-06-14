package com.example.omnigym.exercicio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcessorioRepository extends JpaRepository<Acessorio, Long> {
    Optional<Acessorio> findByNome(String nome);
    boolean existsByNome(String nome);
}
