package com.example.omnigym.financeiro;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
public class PagamentoGatewayController {

    private final PagamentoGatewayService pagamentoGatewayService;

    public PagamentoGatewayController(PagamentoGatewayService pagamentoGatewayService) {
        this.pagamentoGatewayService = pagamentoGatewayService;
    }

    @PostMapping("/instrutor/financeiro/faturas/{faturaId}/processar-pagamento")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<PagamentoGatewayResponseDTO> processarPagamento(
            @PathVariable Long faturaId,
            @Valid @RequestBody PagamentoGatewayRequestDTO dto) {

        PagamentoGatewayResponseDTO response = pagamentoGatewayService.iniciarTransacao(faturaId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/instrutor/financeiro/pagamentos/{pagamentoId}/confirmar")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<Void> confirmarPagamento(
            @PathVariable Long pagamentoId) {

        pagamentoGatewayService.confirmarPagamento(pagamentoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/instrutor/financeiro/pagamentos/{pagamentoId}/recusar")
    @PreAuthorize("hasRole('INSTRUTOR')")
    public ResponseEntity<Void> recusarPagamento(
            @PathVariable Long pagamentoId) {

        pagamentoGatewayService.recusarPagamento(pagamentoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/aluno/financeiro/faturas/{faturaId}/processar-pagamento")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<PagamentoGatewayResponseDTO> processarPagamentoAluno(
            @PathVariable Long faturaId,
            @Valid @RequestBody PagamentoGatewayRequestDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        PagamentoGatewayResponseDTO response = pagamentoGatewayService.iniciarTransacaoAluno(faturaId, dto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/aluno/financeiro/pagamentos/{pagamentoId}/confirmar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Void> confirmarPagamentoAluno(
            @PathVariable Long pagamentoId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        pagamentoGatewayService.confirmarPagamentoAluno(pagamentoId, username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/aluno/financeiro/pagamentos/{pagamentoId}/recusar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<Void> recusarPagamentoAluno(
            @PathVariable Long pagamentoId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        pagamentoGatewayService.recusarPagamentoAluno(pagamentoId, username);
        return ResponseEntity.noContent().build();
    }
}
