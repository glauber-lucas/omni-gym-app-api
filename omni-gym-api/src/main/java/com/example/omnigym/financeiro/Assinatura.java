package com.example.omnigym.financeiro;

import java.util.Date;

import com.example.omnigym.user.User;
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
@Table(name = "assinatura")
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private User aluno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plano_id", nullable = false)
    private Plano plano;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataInicio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAssinatura status = StatusAssinatura.ATIVA;

    @Column(nullable = false)
    private Integer faturasGeradas = 0;

    @Column(nullable = false)
    private Boolean primeiraFaturaGerada = true;

    public Assinatura() {}

    public Assinatura(User aluno, Plano plano, Date dataInicio, Date dataFim) {
        this.aluno = aluno;
        this.plano = plano;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusAssinatura.ATIVA;
        this.faturasGeradas = 0;
        this.primeiraFaturaGerada = true;
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

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public StatusAssinatura getStatus() {
        return status;
    }

    public void setStatus(StatusAssinatura status) {
        this.status = status;
    }

    public Integer getFaturasGeradas() {
        return faturasGeradas;
    }

    public void setFaturasGeradas(Integer faturasGeradas) {
        this.faturasGeradas = faturasGeradas;
    }

    public Boolean getPrimeiraFaturaGerada() {
        return primeiraFaturaGerada;
    }

    public void setPrimeiraFaturaGerada(Boolean primeiraFaturaGerada) {
        this.primeiraFaturaGerada = primeiraFaturaGerada;
    }
}
