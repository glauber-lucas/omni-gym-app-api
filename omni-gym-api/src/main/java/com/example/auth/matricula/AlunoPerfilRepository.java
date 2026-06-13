package com.example.auth.matricula;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import com.example.auth.matricula.AlunoPerfil;
import com.example.auth.matricula.StatusMatricula;
import com.example.auth.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoPerfilRepository extends JpaRepository<AlunoPerfil, Long> {
    Optional<AlunoPerfil> findByUser(User user);
    Optional<AlunoPerfil> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    List<AlunoPerfil> findByStatusMatricula(StatusMatricula status);
}
