package com.example.omnigym.treino;

import com.example.omnigym.core.security.*;
import com.example.omnigym.core.exception.*;
import com.example.omnigym.user.*;
import com.example.omnigym.matricula.*;
import com.example.omnigym.exercicio.*;
import com.example.omnigym.treino.*;
import com.example.omnigym.clinico.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "treino_exercicio")
public class TreinoExercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_treino_id", nullable = false)
    private FichaTreino fichaTreino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false)
    private Integer repeticoes;

    @Column(name = "carga_inicial")
    private String cargaInicial;

    @Column(name = "descanso_segundos")
    private Integer descansoSegundos;

    @Column(name = "ordem_execucao", nullable = false)
    private Integer ordemExecucao;

    @OneToMany(mappedBy = "treinoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObservacaoPedagogica> observacoes = new ArrayList<>();

    public TreinoExercicio() {}

    public TreinoExercicio(FichaTreino fichaTreino, Exercicio exercicio, Integer series,
                           Integer repeticoes, String cargaInicial, Integer descansoSegundos,
                           Integer ordemExecucao) {
        this.fichaTreino = fichaTreino;
        this.exercicio = exercicio;
        this.series = series;
        this.repeticoes = repeticoes;
        this.cargaInicial = cargaInicial;
        this.descansoSegundos = descansoSegundos;
        this.ordemExecucao = ordemExecucao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FichaTreino getFichaTreino() {
        return fichaTreino;
    }

    public void setFichaTreino(FichaTreino fichaTreino) {
        this.fichaTreino = fichaTreino;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }

    public Integer getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(Integer repeticoes) {
        this.repeticoes = repeticoes;
    }

    public String getCargaInicial() {
        return cargaInicial;
    }

    public void setCargaInicial(String cargaInicial) {
        this.cargaInicial = cargaInicial;
    }

    public Integer getDescansoSegundos() {
        return descansoSegundos;
    }

    public void setDescansoSegundos(Integer descansoSegundos) {
        this.descansoSegundos = descansoSegundos;
    }

    public Integer getOrdemExecucao() {
        return ordemExecucao;
    }

    public void setOrdemExecucao(Integer ordemExecucao) {
        this.ordemExecucao = ordemExecucao;
    }

    public List<ObservacaoPedagogica> getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(List<ObservacaoPedagogica> observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreinoExercicio that = (TreinoExercicio) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
