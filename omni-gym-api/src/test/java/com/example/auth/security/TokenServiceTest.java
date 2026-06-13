package com.example.auth.security;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

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

