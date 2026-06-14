package com.example.auth.exercicio;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "exercicio_adaptacao")
public class ExercicioAdaptacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulacao_id", nullable = false)
    private Articulacao articulacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acessorio_id", nullable = false)
    private Acessorio acessorio;

    @Column(name = "instrucao_texto", columnDefinition = "TEXT")
    private String instrucaoTexto;

    public ExercicioAdaptacao() {}

    public ExercicioAdaptacao(Exercicio exercicio, Articulacao articulacao, Acessorio acessorio, String instrucaoTexto) {
        this.exercicio = exercicio;
        this.articulacao = articulacao;
        this.acessorio = acessorio;
        this.instrucaoTexto = instrucaoTexto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Articulacao getArticulacao() {
        return articulacao;
    }

    public void setArticulacao(Articulacao articulacao) {
        this.articulacao = articulacao;
    }

    public Acessorio getAcessorio() {
        return acessorio;
    }

    public void setAcessorio(Acessorio acessorio) {
        this.acessorio = acessorio;
    }

    public String getInstrucaoTexto() {
        return instrucaoTexto;
    }

    public void setInstrucaoTexto(String instrucaoTexto) {
        this.instrucaoTexto = instrucaoTexto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExercicioAdaptacao that = (ExercicioAdaptacao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
