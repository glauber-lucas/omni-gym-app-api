package com.example.omnigym.clinico;

public enum TipoDocumentoMedico {
    LAUDO_MEDICO("Laudo Médico"),
    EXAME_DIAGNOSTICO("Exame de Diagnóstico"),
    PARECER_CLINICO("Parecer Clínico"),
    RELATORIO_FISIOTERAPIA("Relatório de Fisioterapia"),
    RECEITA_MEDICA("Receita Médica"),
    ATESTADO("Atestado"),
    OUTRO("Outro");

    private final String descricao;

    TipoDocumentoMedico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
