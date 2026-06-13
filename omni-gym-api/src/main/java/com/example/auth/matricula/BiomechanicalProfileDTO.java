package com.example.auth.matricula;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record BiomechanicalProfileDTO(
        @NotBlank(message = "Estabilidade de tronco é obrigatória")
        String estabilidadeTronco,

        List<Long> restricoesIds,

        Boolean bloqueioMedico
) {}
