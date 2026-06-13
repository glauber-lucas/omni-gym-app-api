package com.example.auth.treino;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import com.example.auth.treino.FichaTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FichaTreinoRepository extends JpaRepository<FichaTreino, Long> {
    List<FichaTreino> findByAlunoId(Long alunoId);
    List<FichaTreino> findByAlunoIdAndAtivaTrue(Long alunoId);
    List<FichaTreino> findByInstrutorId(Long instrutorId);
}
