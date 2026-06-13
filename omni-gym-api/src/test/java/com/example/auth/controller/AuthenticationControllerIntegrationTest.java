package com.example.auth.controller;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.example.auth.user.UserRepository userRepository;

    @org.junit.jupiter.api.BeforeEach
    void cleanUp() {
        userRepository.findByUsername("integ@example.com").ifPresent(userRepository::delete);
        userRepository.findByUsername("fail@example.com").ifPresent(userRepository::delete);
    }

    @Test
    void registrarEAutenticarFluxoBasico() throws Exception {
        Map<String, String> registro = Map.of("usuario", "integ@example.com", "senha", "s3cretP@ss");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuário registrado"));

        Map<String, String> login = Map.of("usuario", "integ@example.com", "senha", "s3cretP@ss");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String resp = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(resp);

        assertThat(node.get("token").asText()).isNotBlank();
        assertThat(node.get("tipoToken").asText()).isEqualTo("Bearer");
        assertThat(node.get("expiraEmMillis").asLong()).isGreaterThan(0);
    }

    @Test
    void falhaLoginSenhaErrada() throws Exception {
        Map<String, String> registro = Map.of("usuario", "fail@example.com", "senha", "correctP@ss");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated());

        Map<String, String> loginErrado = Map.of("usuario", "fail@example.com", "senha", "wrong");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginErrado)))
                .andExpect(status().is4xxClientError());
    }
}

