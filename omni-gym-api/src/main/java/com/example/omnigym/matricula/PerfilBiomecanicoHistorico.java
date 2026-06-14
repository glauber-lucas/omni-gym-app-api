package com.example.omnigym.matricula;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.example.omnigym.exercicio.Articulacao;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "perfil_biomecanico_historico")
public class PerfilBiomecanicoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private AlunoPerfil alunoPerfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "estabilidade_tronco")
    private EstabilidadeTronco estabilidadeTronco;

    @Column(name = "bloqueio_medico", nullable = false)
    private Boolean bloqueioMedico = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "historico_restricao",
        joinColumns = @JoinColumn(name = "historico_id"),
        inverseJoinColumns = @JoinColumn(name = "articulacao_id")
    )
    private Set<Articulacao> restricoes = new HashSet<>();

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_reavaliacao", nullable = true)
    private LocalDateTime dataReavaliacao;

    @Column(name = "observacoes", length = 1000, nullable = true)
    private String observacoes;

    // Construtores
    public PerfilBiomecanicoHistorico() {}

    public PerfilBiomecanicoHistorico(AlunoPerfil alunoPerfil, EstabilidadeTronco estabilidadeTronco,
            Boolean bloqueioMedico, Set<Articulacao> restricoes) {
        this.alunoPerfil = alunoPerfil;
        this.estabilidadeTronco = estabilidadeTronco;
        this.bloqueioMedico = bloqueioMedico;
        this.restricoes = restricoes;
        this.dataCriacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlunoPerfil getAlunoPerfil() {
        return alunoPerfil;
    }

    public void setAlunoPerfil(AlunoPerfil alunoPerfil) {
        this.alunoPerfil = alunoPerfil;
    }

    public EstabilidadeTronco getEstabilidadeTronco() {
        return estabilidadeTronco;
    }

    public void setEstabilidadeTronco(EstabilidadeTronco estabilidadeTronco) {
        this.estabilidadeTronco = estabilidadeTronco;
    }

    public Boolean getBloqueioMedico() {
        return bloqueioMedico;
    }

    public void setBloqueioMedico(Boolean bloqueioMedico) {
        this.bloqueioMedico = bloqueioMedico;
    }

    public Set<Articulacao> getRestricoes() {
        return restricoes;
    }

    public void setRestricoes(Set<Articulacao> restricoes) {
        this.restricoes = restricoes;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataReavaliacao() {
        return dataReavaliacao;
    }

    public void setDataReavaliacao(LocalDateTime dataReavaliacao) {
        this.dataReavaliacao = dataReavaliacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
