package com.example.auth.clinico;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "dossie_clinico")
public class DossieClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private User aluno;

    @Column(name = "laudo_medico_url")
    private String laudoMedicoUrl;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_avaliacao")
    private Date dataAvaliacao;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_proxima_reavaliacao")
    private Date dataProximaReavaliacao;

    public DossieClinico() {}

    public DossieClinico(User aluno, String laudoMedicoUrl, String observacoes,
                         Date dataAvaliacao, Date dataProximaReavaliacao) {
        this.aluno = aluno;
        this.laudoMedicoUrl = laudoMedicoUrl;
        this.observacoes = observacoes;
        this.dataAvaliacao = dataAvaliacao;
        this.dataProximaReavaliacao = dataProximaReavaliacao;
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

    public String getLaudoMedicoUrl() {
        return laudoMedicoUrl;
    }

    public void setLaudoMedicoUrl(String laudoMedicoUrl) {
        this.laudoMedicoUrl = laudoMedicoUrl;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Date dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public Date getDataProximaReavaliacao() {
        return dataProximaReavaliacao;
    }

    public void setDataProximaReavaliacao(Date dataProximaReavaliacao) {
        this.dataProximaReavaliacao = dataProximaReavaliacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DossieClinico that = (DossieClinico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
