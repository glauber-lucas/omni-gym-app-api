package com.example.omnigym.financeiro;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagamentoGatewayService {

    private final PagamentoGatewayRepository pagamentoRepository;
    private final FaturaRepository faturaRepository;

    public PagamentoGatewayService(PagamentoGatewayRepository pagamentoRepository,
                                  FaturaRepository faturaRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.faturaRepository = faturaRepository;
    }

    @Transactional
    public PagamentoGatewayResponseDTO iniciarTransacao(Long faturaId, PagamentoGatewayRequestDTO dto) {
        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada com ID: " + faturaId));

        ProviderPagamento provedor = ProviderPagamento.valueOf(dto.provedor().toUpperCase());

        PagamentoGateway pagamento = new PagamentoGateway(fatura, provedor);
        String idTransacao = gerarIdTransacao(provedor);
        pagamento.setIdTransacaoExterna(idTransacao);
        pagamento.setStatus(StatusPagamento.PENDENTE);

        PagamentoGateway saved = pagamentoRepository.save(pagamento);

        String urlPagamento = gerarUrlPagamento(provedor, idTransacao, fatura.getValor().toPlainString());

        return new PagamentoGatewayResponseDTO(
            saved.getId(),
            saved.getFatura().getId(),
            saved.getProvedor().name(),
            saved.getStatus().name(),
            urlPagamento,
            saved.getIdTransacaoExterna(),
            saved.getDataCriacao()
        );
    }

    @Transactional
    public void confirmarPagamento(Long pagamentoId) {
        PagamentoGateway pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado com ID: " + pagamentoId));

        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamento.setDataProcessamento(new Date());
        pagamentoRepository.save(pagamento);

        Fatura fatura = pagamento.getFatura();
        fatura.setStatus("PAGO");
        fatura.setValorPago(fatura.getValor().subtract(fatura.getDesconto()));
        fatura.setDataPagamento(new Date());
        faturaRepository.save(fatura);
    }

    @Transactional
    public void recusarPagamento(Long pagamentoId) {
        PagamentoGateway pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado com ID: " + pagamentoId));

        pagamento.setStatus(StatusPagamento.RECUSADO);
        pagamento.setDataProcessamento(new Date());
        pagamento.setTentativas(pagamento.getTentativas() + 1);
        pagamentoRepository.save(pagamento);
    }

    @Transactional
    public PagamentoGatewayResponseDTO iniciarTransacaoAluno(Long faturaId, PagamentoGatewayRequestDTO dto, String username) {
        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada com ID: " + faturaId));

        if (!fatura.getAluno().getUsername().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso negado. Você só pode processar pagamentos de suas próprias faturas.");
        }

        return iniciarTransacao(faturaId, dto);
    }

    @Transactional
    public void confirmarPagamentoAluno(Long pagamentoId, String username) {
        PagamentoGateway pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado com ID: " + pagamentoId));

        if (!pagamento.getFatura().getAluno().getUsername().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso negado. Você só pode confirmar pagamentos de suas próprias faturas.");
        }

        confirmarPagamento(pagamentoId);
    }

    @Transactional
    public void recusarPagamentoAluno(Long pagamentoId, String username) {
        PagamentoGateway pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado com ID: " + pagamentoId));

        if (!pagamento.getFatura().getAluno().getUsername().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso negado. Você só pode recusar pagamentos de suas próprias faturas.");
        }

        recusarPagamento(pagamentoId);
    }

    private String gerarIdTransacao(ProviderPagamento provedor) {
        String prefix = switch (provedor) {
            case STRIPE -> "stripe_ch_";
            case PAYPAL -> "paypal_";
            case MERCADO_PAGO -> "mp_";
        };
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

    private String gerarUrlPagamento(ProviderPagamento provedor, String idTransacao, String valor) {
        return switch (provedor) {
            case STRIPE -> "https://gateway-simulado.test/stripe/pay/" + idTransacao + "?amount=" + valor;
            case PAYPAL -> "https://gateway-simulado.test/paypal/pay/" + idTransacao + "?amount=" + valor;
            case MERCADO_PAGO -> "https://gateway-simulado.test/mercadopago/pay/" + idTransacao + "?amount=" + valor;
        };
    }
}
