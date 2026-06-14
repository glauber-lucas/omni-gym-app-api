package com.example.omnigym.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.example.omnigym.core.security.TokenService;
import com.example.omnigym.financeiro.FaturaDTO;
import com.example.omnigym.financeiro.FaturaRepository;
import com.example.omnigym.financeiro.Plano;
import com.example.omnigym.financeiro.PlanoDTO;
import com.example.omnigym.financeiro.PlanoRepository;
import com.example.omnigym.user.Role;
import com.example.omnigym.user.User;
import com.example.omnigym.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FinanceiroControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanoRepository planoRepository;

    @Autowired
    private FaturaRepository faturaRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private User aluno;
    private User instrutor;
    private String tokenAluno;
    private String tokenInstrutor;

    @BeforeEach
    void setUp() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        aluno = new User("aluno_fin_" + randomSuffix + "@example.com", "password", Role.ROLE_ALUNO);
        aluno.setName("Aluno Financeiro");
        aluno.setDocumentId("doc_al_" + randomSuffix);
        aluno = userRepository.save(aluno);

        instrutor = new User("instrutor_fin_" + randomSuffix + "@example.com", "password", Role.ROLE_INSTRUTOR);
        instrutor.setName("Instrutor Financeiro");
        instrutor.setDocumentId("doc_in_" + randomSuffix);
        instrutor = userRepository.save(instrutor);

        tokenAluno = tokenService.generateToken(aluno);
        tokenInstrutor = tokenService.generateToken(instrutor);
    }

    @Test
    void instrutorPodeCadastrarEDetalharPlanos() throws Exception {
        PlanoDTO planoDTO = new PlanoDTO("PLANO SUPER INCLUSIVO", new BigDecimal("150.00"), 3);

        // Cadastra plano
        String responseContent = mockMvc.perform(post("/instrutor/financeiro/planos")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planoDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseContent);
        assertThat(jsonNode.get("nome").asText()).isEqualTo("PLANO SUPER INCLUSIVO");
        assertThat(jsonNode.get("valor").asDouble()).isEqualTo(150.00);

        // Lista planos
        mockMvc.perform(get("/instrutor/financeiro/planos")
                        .header("Authorization", "Bearer " + tokenInstrutor))
                .andExpect(status().isOk());
    }

    @Test
    void instrutorPodeCadastrarFaturaManualmente() throws Exception {
        Plano plano = planoRepository.save(new Plano("MENSAL FLEX", new BigDecimal("120.00"), 1));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15);
        Date vencimento = cal.getTime();

        FaturaDTO faturaDTO = new FaturaDTO(plano.getId(), null, vencimento);

        // Cadastra fatura
        String responseContent = mockMvc.perform(post("/instrutor/financeiro/alunos/" + aluno.getId() + "/faturas")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faturaDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseContent);
        assertThat(jsonNode.get("alunoId").asLong()).isEqualTo(aluno.getId());
        assertThat(jsonNode.get("valorOriginal").asDouble()).isEqualTo(120.00);
        assertThat(jsonNode.get("status").asText()).isEqualTo("PENDENTE");
    }

    @Test
    void instrutorPodeAplicarDescontoERegistrarPagamento() throws Exception {
        Plano plano = planoRepository.save(new Plano("PLAN ANUAL", new BigDecimal("1000.00"), 12));
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        
        FaturaDTO faturaDTO = new FaturaDTO(plano.getId(), null, cal.getTime());

        String responseFatura = mockMvc.perform(post("/instrutor/financeiro/alunos/" + aluno.getId() + "/faturas")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faturaDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode faturaNode = objectMapper.readTree(responseFatura);
        Long faturaId = faturaNode.get("id").asLong();

        // Aplica desconto de R$ 100.00
        mockMvc.perform(post("/instrutor/financeiro/faturas/" + faturaId + "/desconto")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("desconto", new BigDecimal("100.00")))))
                .andExpect(status().isOk());

        // Paga a fatura com valor de R$ 900.00
        String responsePagamento = mockMvc.perform(post("/instrutor/financeiro/faturas/" + faturaId + "/pagar")
                        .header("Authorization", "Bearer " + tokenInstrutor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("valorPago", new BigDecimal("900.00")))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode pagadoNode = objectMapper.readTree(responsePagamento);
        assertThat(pagadoNode.get("status").asText()).isEqualTo("PAGO");
        assertThat(pagadoNode.get("desconto").asDouble()).isEqualTo(100.00);
        assertThat(pagadoNode.get("valorPago").asDouble()).isEqualTo(900.00);
    }

    @Test
    void alunoNaoPodeAcessarRotasFinanceiras() throws Exception {
        mockMvc.perform(get("/instrutor/financeiro/relatorio-faturamento")
                        .header("Authorization", "Bearer " + tokenAluno))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/instrutor/financeiro/planos")
                        .header("Authorization", "Bearer " + tokenAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PlanoDTO("P", BigDecimal.TEN, 1))))
                .andExpect(status().isForbidden());
    }
}
