package com.example.omnigym.clinico;

import java.util.Date;
import java.util.Objects;

import com.example.omnigym.treino.TreinoExercicio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "observacao_pedagogica")
public class ObservacaoPedagogica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_exercicio_id", nullable = false)
    private TreinoExercicio treinoExercicio;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_criacao", nullable = false)
    private Date dataCriacao = new Date();

    public ObservacaoPedagogica() {}

    public ObservacaoPedagogica(TreinoExercicio treinoExercicio, String texto) {
        this.treinoExercicio = treinoExercicio;
        this.texto = texto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TreinoExercicio getTreinoExercicio() {
        return treinoExercicio;
    }

    public void setTreinoExercicio(TreinoExercicio treinoExercicio) {
        this.treinoExercicio = treinoExercicio;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObservacaoPedagogica that = (ObservacaoPedagogica) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
