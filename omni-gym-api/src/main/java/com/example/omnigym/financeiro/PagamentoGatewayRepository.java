package com.example.omnigym.financeiro;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoGatewayRepository extends JpaRepository<PagamentoGateway, Long> {
    List<PagamentoGateway> findByFaturaId(Long faturaId);
    List<PagamentoGateway> findByStatus(StatusPagamento status);
    Optional<PagamentoGateway> findByIdTransacaoExterna(String idTransacaoExterna);
}
