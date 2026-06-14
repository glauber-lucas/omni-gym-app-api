package com.example.omnigym.financeiro;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "pagamento_gateway")
public class PagamentoGateway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fatura_id", nullable = false)
    private Fatura fatura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderPagamento provedor;

    @Column(name = "id_transacao_externa")
    private String idTransacaoExterna;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataCriacao = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataProcessamento;

    @Column(nullable = false)
    private Integer tentativas = 0;

    public PagamentoGateway() {}

    public PagamentoGateway(Fatura fatura, ProviderPagamento provedor) {
        this.fatura = fatura;
        this.provedor = provedor;
        this.status = StatusPagamento.PENDENTE;
        this.dataCriacao = new Date();
        this.tentativas = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Fatura getFatura() {
        return fatura;
    }

    public void setFatura(Fatura fatura) {
        this.fatura = fatura;
    }

    public ProviderPagamento getProvedor() {
        return provedor;
    }

    public void setProvedor(ProviderPagamento provedor) {
        this.provedor = provedor;
    }

    public String getIdTransacaoExterna() {
        return idTransacaoExterna;
    }

    public void setIdTransacaoExterna(String idTransacaoExterna) {
        this.idTransacaoExterna = idTransacaoExterna;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(Date dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public Integer getTentativas() {
        return tentativas;
    }

    public void setTentativas(Integer tentativas) {
        this.tentativas = tentativas;
    }
}
