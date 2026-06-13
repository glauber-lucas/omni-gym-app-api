package com.example.auth.matricula;

import com.example.auth.core.security.*;
import com.example.auth.core.exception.*;
import com.example.auth.user.*;
import com.example.auth.matricula.*;
import com.example.auth.exercicio.*;
import com.example.auth.treino.*;
import com.example.auth.clinico.*;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "aluno_perfil")
public class AlunoPerfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column
    private String telefone;

    @Column
    private String endereco;

    @Column(name = "contato_emergencia")
    private String contatoEmergencia;

    @Column(name = "info_familiar")
    private String infoFamiliar;

    @Column
    private String medicamentos;

    @Column
    private String deficiencias;

    @Column
    private String alergias;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_matricula", nullable = false)
    private StatusMatricula statusMatricula = StatusMatricula.AGUARDANDO_HOMOLOGACAO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estabilidade_tronco")
    private EstabilidadeTronco estabilidadeTronco;

    @Column(name = "bloqueio_medico", nullable = false)
    private Boolean bloqueioMedico = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "aluno_restricao",
        joinColumns = @JoinColumn(name = "aluno_id"),
        inverseJoinColumns = @JoinColumn(name = "articulacao_id")
    )
    private Set<Articulacao> restricoes = new HashSet<>();

    public AlunoPerfil() {}

    public AlunoPerfil(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getContatoEmergencia() {
        return contatoEmergencia;
    }

    public void setContatoEmergencia(String contatoEmergencia) {
        this.contatoEmergencia = contatoEmergencia;
    }

    public String getInfoFamiliar() {
        return infoFamiliar;
    }

    public void setInfoFamiliar(String infoFamiliar) {
        this.infoFamiliar = infoFamiliar;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getDeficiencias() {
        return deficiencias;
    }

    public void setDeficiencias(String deficiencias) {
        this.deficiencias = deficiencias;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public StatusMatricula getStatusMatricula() {
        return statusMatricula;
    }

    public void setStatusMatricula(StatusMatricula statusMatricula) {
        this.statusMatricula = statusMatricula;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlunoPerfil that = (AlunoPerfil) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
