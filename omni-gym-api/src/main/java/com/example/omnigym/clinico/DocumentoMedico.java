package com.example.omnigym.clinico;

import java.time.LocalDateTime;
import java.util.Objects;

import com.example.omnigym.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "documento_medico")
public class DocumentoMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private User aluno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoDocumentoMedico tipo;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "mime_type", length = 50)
    private String mimeType;

    @Column(name = "tamanho_bytes")
    private Long tamanhoBytes;

    @Column(name = "arquivo_path", nullable = false, length = 500)
    private String caminhoArquivo;

    @Column(name = "arquivo_hash", length = 64)
    private String hashArquivo;

    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload;

    @Column(name = "data_proxima_reavaliacao", nullable = true)
    private LocalDateTime dataProximaReavaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por", nullable = false)
    private User criadoPor;

    @Column(name = "acessos_count")
    private Integer acessosCount = 0;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    // Construtores
    public DocumentoMedico() {}

    public DocumentoMedico(User aluno, TipoDocumentoMedico tipo, String descricao,
            String mimeType, Long tamanhoBytes, String caminhoArquivo, String hashArquivo,
            LocalDateTime dataProximaReavaliacao, User criadoPor) {
        this.aluno = aluno;
        this.tipo = tipo;
        this.descricao = descricao;
        this.mimeType = mimeType;
        this.tamanhoBytes = tamanhoBytes;
        this.caminhoArquivo = caminhoArquivo;
        this.hashArquivo = hashArquivo;
        this.dataUpload = LocalDateTime.now();
        this.dataProximaReavaliacao = dataProximaReavaliacao;
        this.criadoPor = criadoPor;
        this.acessosCount = 0;
        this.ativo = true;
    }

    // Getters e Setters
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

    public TipoDocumentoMedico getTipo() {
        return tipo;
    }

    public void setTipo(TipoDocumentoMedico tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public void setTamanhoBytes(Long tamanhoBytes) {
        this.tamanhoBytes = tamanhoBytes;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getHashArquivo() {
        return hashArquivo;
    }

    public void setHashArquivo(String hashArquivo) {
        this.hashArquivo = hashArquivo;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }

    public LocalDateTime getDataProximaReavaliacao() {
        return dataProximaReavaliacao;
    }

    public void setDataProximaReavaliacao(LocalDateTime dataProximaReavaliacao) {
        this.dataProximaReavaliacao = dataProximaReavaliacao;
    }

    public User getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(User criadoPor) {
        this.criadoPor = criadoPor;
    }

    public Integer getAcessosCount() {
        return acessosCount;
    }

    public void setAcessosCount(Integer acessosCount) {
        this.acessosCount = acessosCount;
    }

    public void incrementarAcessos() {
        this.acessosCount = (this.acessosCount != null ? this.acessosCount : 0) + 1;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentoMedico that = (DocumentoMedico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
