package com.example.omnigym.security;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Test
    void gerarValidarELerUsuarioDoToken() {
        UserDetails usuario = User.withUsername("teste@example.com").password("ignored").roles("ALUNO").build();
        String token = tokenService.generateToken(usuario);

        assertNotNull(token);
        assertTrue(tokenService.validateToken(token));
        assertEquals("teste@example.com", tokenService.getUsernameFromToken(token));
    }
}

