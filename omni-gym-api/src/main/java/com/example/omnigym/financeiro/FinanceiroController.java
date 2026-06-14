package com.example.omnigym.financeiro;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
public class FinanceiroController {

    private final FinanceiroService financeiroService;

    public FinanceiroController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @PostMapping("/instrutor/financeiro/planos")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<Plano> cadastrarPlano(@Valid @RequestBody PlanoDTO dto) {
        Plano plano = financeiroService.cadastrarPlano(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(plano);
    }

    @GetMapping("/instrutor/financeiro/planos")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<List<Plano>> listarPlanos() {
        List<Plano> planos = financeiroService.listarPlanos();
        return ResponseEntity.ok(planos);
    }

    @PostMapping("/instrutor/financeiro/alunos/{alunoId}/faturas")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<FaturaResponseDTO> cadastrarFatura(
            @PathVariable Long alunoId,
            @Valid @RequestBody FaturaDTO dto) {
        FaturaResponseDTO fatura = financeiroService.cadastrarFatura(alunoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(fatura);
    }

    @GetMapping("/instrutor/financeiro/faturas")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<List<FaturaResponseDTO>> listarFaturas(
            @RequestParam(required = false) String status) {
        List<FaturaResponseDTO> response = financeiroService.listarFaturas(status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/instrutor/financeiro/faturas/{faturaId}/pagar")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<FaturaResponseDTO> registrarPagamento(
            @PathVariable Long faturaId,
            @RequestBody(required = false) java.util.Map<String, BigDecimal> body) {
        BigDecimal valorPago = null;
        if (body != null && body.containsKey("valorPago")) {
            valorPago = body.get("valorPago");
        }
        FaturaResponseDTO response = financeiroService.registrarPagamento(faturaId, valorPago);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/instrutor/financeiro/faturas/{faturaId}/desconto")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<FaturaResponseDTO> aplicarDesconto(
            @PathVariable Long faturaId,
            @RequestBody java.util.Map<String, BigDecimal> body) {
        if (body == null || !body.containsKey("desconto")) {
            throw new IllegalArgumentException("Valor do desconto é obrigatório.");
        }
        BigDecimal desconto = body.get("desconto");
        FaturaResponseDTO response = financeiroService.aplicarDesconto(faturaId, desconto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instrutor/financeiro/relatorio-faturamento")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<RelatorioFaturamentoDTO> obterRelatorioFaturamento() {
        RelatorioFaturamentoDTO relatorio = financeiroService.obterRelatorioFaturamento();
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/aluno/financeiro/faturas")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<FaturaResponseDTO>> listarMinhasFaturas(
            @RequestParam(required = false) String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<FaturaResponseDTO> response = financeiroService.listarFaturasPorAluno(username, status);
        return ResponseEntity.ok(response);
    }
}
