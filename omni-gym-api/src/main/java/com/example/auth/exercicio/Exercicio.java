package com.example.auth.exercicio;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.example.auth.matricula.EstabilidadeTronco;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "exercicio")
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "grupo_muscular")
    private String grupoMuscular;

    @Column(name = "estacao_trabalho")
    private String estacaoTrabalho;

    @Enumerated(EnumType.STRING)
    @Column(name = "estabilidade_tronco_minima", nullable = false)
    private EstabilidadeTronco estabilidadeTroncoMinima = EstabilidadeTronco.LIMITADO;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "exercicio_exigencia",
        joinColumns = @JoinColumn(name = "exercicio_id"),
        inverseJoinColumns = @JoinColumn(name = "articulacao_id")
    )
    private Set<Articulacao> exigencias = new HashSet<>();

    public Exercicio() {}

    public Exercicio(String nome, String grupoMuscular, String estacaoTrabalho, EstabilidadeTronco estabilidadeTroncoMinima) {
        this.nome = nome;
        this.grupoMuscular = grupoMuscular;
        this.estacaoTrabalho = estacaoTrabalho;
        this.estabilidadeTroncoMinima = estabilidadeTroncoMinima;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public String getEstacaoTrabalho() {
        return estacaoTrabalho;
    }

    public void setEstacaoTrabalho(String estacaoTrabalho) {
        this.estacaoTrabalho = estacaoTrabalho;
    }

    public EstabilidadeTronco getEstabilidadeTroncoMinima() {
        return estabilidadeTroncoMinima;
    }

    public void setEstabilidadeTroncoMinima(EstabilidadeTronco estabilidadeTroncoMinima) {
        this.estabilidadeTroncoMinima = estabilidadeTroncoMinima;
    }

    public Set<Articulacao> getExigencias() {
        return exigencias;
    }

    public void setExigencias(Set<Articulacao> exigencias) {
        this.exigencias = exigencias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercicio exercicio = (Exercicio) o;
        return Objects.equals(id, exercicio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
