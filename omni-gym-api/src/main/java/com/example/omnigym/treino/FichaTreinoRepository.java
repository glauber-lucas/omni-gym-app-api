package com.example.omnigym.treino;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

import com.example.omnigym.treino.FichaTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FichaTreinoRepository extends JpaRepository<FichaTreino, Long> {
    List<FichaTreino> findByAlunoId(Long alunoId);
    List<FichaTreino> findByAlunoIdAndAtivaTrue(Long alunoId);
    List<FichaTreino> findByInstrutorId(Long instrutorId);
}
