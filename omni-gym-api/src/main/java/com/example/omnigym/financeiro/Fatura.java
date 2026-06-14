package com.example.omnigym.financeiro;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import com.example.omnigym.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "fatura")
public class Fatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private User aluno;

    @ManyToOne
    @JoinColumn(name = "plano_id")
    private Plano plano;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column
    private BigDecimal valorPago;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dataVencimento;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date dataPagamento;

    @Column(nullable = false)
    private String status; // PENDENTE, PAGO, ATRASADO

    public Fatura() {}

    public Fatura(User aluno, Plano plano, BigDecimal valor, Date dataVencimento, String status) {
        this.aluno = aluno;
        this.plano = plano;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.desconto = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAluno() {
        return aluno;
    }

    public void setAluno(User aluno) {
        this.aluno = aluno;
    }

    public Plano getPlano() {
        return plano;
    }

    public void setPlano(Plano plano) {
        this.plano = plano;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fatura fatura = (Fatura) o;
        return Objects.equals(id, fatura.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
