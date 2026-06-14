package com.example.auth.matricula;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.auth.user.User;

@Repository
public interface AlunoPerfilRepository extends JpaRepository<AlunoPerfil, Long> {
    Optional<AlunoPerfil> findByUser(User user);
    Optional<AlunoPerfil> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    List<AlunoPerfil> findByStatusMatricula(StatusMatricula status);
}
