package com.example.omnigym.user;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

public record MeUserDTO(Long id, String documentId, String name, String email, String role) {}
