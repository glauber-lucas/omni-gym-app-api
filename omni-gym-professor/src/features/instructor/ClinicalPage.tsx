import { useMutation, useQueries, useQuery, useQueryClient } from '@tanstack/react-query';
import { Download, Eye, FilePlus2, FileText, Trash2 } from 'lucide-react';
import { useState } from 'react';
import { instructorApi } from '@/services/api/instructorApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { Field, Textarea } from '@/shared/components/Field';
import { apiError, date } from '@/shared/utils/format';

const documentTypes = ['', 'LAUDO_MEDICO', 'EXAME_DIAGNOSTICO', 'PARECER_CLINICO', 'RELATORIO_FISIOTERAPIA', 'RECEITA_MEDICA', 'ATESTADO', 'OUTRO'];

export function ClinicalPage() {
  const queryClient = useQueryClient();
  const [students] = useQueries({
    queries: [{ queryKey: ['instructor', 'students'], queryFn: instructorApi.students }]
  });
  const [alunoId, setAlunoId] = useState('');
  const [tipo, setTipo] = useState('');
  const [selectedDocumentId, setSelectedDocumentId] = useState<number | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [record, setRecord] = useState({
    laudoMedicoUrl: '',
    observacoes: '',
    dataAvaliacao: '',
    dataProximaReavaliacao: ''
  });

  const docs = useQuery({
    queryKey: ['instructor', 'documents', alunoId, tipo],
    queryFn: () => instructorApi.documents(Number(alunoId), tipo || undefined),
    enabled: Boolean(alunoId)
  });

  const records = useQuery({
    queryKey: ['instructor', 'clinical-records', alunoId],
    queryFn: () => instructorApi.clinicalRecords(Number(alunoId)),
    enabled: Boolean(alunoId)
  });

  const audit = useQuery({
    queryKey: ['instructor', 'document-audit', selectedDocumentId],
    queryFn: () => instructorApi.audit(Number(selectedDocumentId)),
    enabled: Boolean(selectedDocumentId)
  });

  const createRecord = useMutation({
    mutationFn: () => {
      if (!alunoId) throw new Error('Selecione um aluno.');
      return instructorApi.createClinicalRecord(Number(alunoId), record);
    },
    onSuccess: async () => {
      setMessage('Dossiê clínico cadastrado.');
      setRecord({ laudoMedicoUrl: '', observacoes: '', dataAvaliacao: '', dataProximaReavaliacao: '' });
      await queryClient.invalidateQueries({ queryKey: ['instructor', 'clinical-records', alunoId] });
    },
    onError: error => setMessage(apiError(error))
  });

  const removeDocument = useMutation({
    mutationFn: instructorApi.deleteDocument,
    onSuccess: async () => {
      setMessage('Documento removido.');
      await queryClient.invalidateQueries({ queryKey: ['instructor', 'documents'] });
    },
    onError: error => setMessage(apiError(error))
  });

  return (
    <div className="page-shell">
      <header className="glass-panel">
        <p className="muted">Módulo clínico</p>
        <div className="mt-2 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="text-3xl font-black text-ink-100">Dossiês, documentos e auditoria</h2>
            <p className="muted mt-2 max-w-2xl">Centralize evidências clínicas, reavaliações e histórico de acesso dos documentos.</p>
          </div>
          <span className="badge bg-primary-10 text-primary-100">{alunoId ? 'Aluno selecionado' : 'Selecione um aluno'}</span>
        </div>
      </header>

      {message && <div className="status-banner">{message}</div>}

      <section className="grid gap-5 xl:grid-cols-[420px_1fr]">
        <aside className="glass-panel h-fit space-y-4">
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-primary-20 text-primary-100">
              <FilePlus2 size={20} />
            </div>
            <div>
              <h3 className="section-title">Contexto do aluno</h3>
              <p className="muted">Escolha um aluno antes de consultar documentos.</p>
            </div>
          </div>
          <label className="grid gap-1.5">
            <span className="label">Aluno</span>
            <select className="input" value={alunoId} onChange={event => setAlunoId(event.target.value)}>
              <option value="">Selecione</option>
              {(students.data ?? []).map(student => (
                <option key={student.userId ?? student.id} value={student.userId ?? student.id}>
                  {student.name ?? student.username}
                </option>
              ))}
            </select>
          </label>

          <form
            className="space-y-3 rounded-[1.35rem] border border-primary-20 bg-primary-10/60 p-4"
            onSubmit={event => {
              event.preventDefault();
              createRecord.mutate();
            }}
          >
            <p className="font-black text-ink-100">Novo dossiê</p>
            <Field label="URL do laudo" value={record.laudoMedicoUrl} onChange={event => setRecord({ ...record, laudoMedicoUrl: event.target.value })} required />
            <Field label="Data da avaliação" type="date" value={record.dataAvaliacao} onChange={event => setRecord({ ...record, dataAvaliacao: event.target.value })} required />
            <Field
              label="Próxima reavaliação"
              type="date"
              value={record.dataProximaReavaliacao}
              onChange={event => setRecord({ ...record, dataProximaReavaliacao: event.target.value })}
              required
            />
            <Textarea label="Observações" value={record.observacoes} onChange={event => setRecord({ ...record, observacoes: event.target.value })} />
            <Button className="w-full justify-center" isLoading={createRecord.isPending}>
              <FilePlus2 size={16} />
              Cadastrar dossiê
            </Button>
          </form>
        </aside>

        <div className="grid gap-5">
          <section className="panel">
            <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <div className="flex items-center gap-3">
                <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-secondary-20 text-secondary-100">
                  <FileText size={20} />
                </div>
                <h3 className="section-title">Documentos médicos</h3>
              </div>
              <select className="input w-full sm:w-72" value={tipo} onChange={event => setTipo(event.target.value)} disabled={!alunoId}>
                {documentTypes.map(item => (
                  <option key={item || 'todos'} value={item}>
                    {item ? item.replaceAll('_', ' ') : 'Todos os tipos'}
                  </option>
                ))}
              </select>
            </div>
            <div className="space-y-3">
              {(docs.data ?? []).map(document => (
                <div key={document.id} className="soft-card flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                  <div>
                    <p className="font-black text-ink-100">{document.tipo.replaceAll('_', ' ')}</p>
                    <p className="muted">
                      {document.descricao || 'Sem descrição'} · {date(document.dataUpload)} · {document.acessosCount ?? 0} acessos
                    </p>
                  </div>
                  <div className="flex gap-2">
                    <a className="icon-button" href={instructorApi.documentDownloadUrl(document.id)} target="_blank" rel="noreferrer" aria-label="Baixar documento">
                      <Download size={16} />
                    </a>
                    <button className="icon-button" onClick={() => setSelectedDocumentId(document.id)} aria-label="Ver auditoria">
                      <Eye size={16} />
                    </button>
                    <button className="icon-button" onClick={() => removeDocument.mutate(document.id)} aria-label="Excluir documento">
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>
              ))}
              {alunoId && !docs.data?.length && <EmptyState title="Nenhum documento encontrado." />}
              {!alunoId && <EmptyState title="Selecione um aluno para consultar documentos." />}
            </div>
          </section>

          <section className="grid gap-5 lg:grid-cols-2">
            <div className="panel">
              <h3 className="section-title">Dossiês cadastrados</h3>
              <div className="mt-4 space-y-3">
                {(records.data ?? []).map(item => (
                  <div key={item.id} className="soft-card">
                    <p className="font-black text-ink-100">{date(item.dataAvaliacao)}</p>
                    <p className="muted">Próxima: {date(item.dataProximaReavaliacao)}</p>
                    <p className="mt-2 text-sm text-ink-60">{item.observacoes || 'Sem observações.'}</p>
                  </div>
                ))}
                {alunoId && !records.data?.length && <EmptyState title="Nenhum dossiê cadastrado." />}
              </div>
            </div>

            <div className="panel">
              <h3 className="section-title">Auditoria de acesso</h3>
              <div className="mt-4 space-y-3">
                {(audit.data ?? []).map(item => (
                  <div key={item.id} className="soft-card">
                    <p className="font-black text-ink-100">{item.username ?? 'Usuário'}</p>
                    <p className="muted">{date(item.dataAcesso)} · {item.ipAddress ?? 'IP não informado'}</p>
                  </div>
                ))}
                {!selectedDocumentId && <EmptyState title="Selecione um documento para ver auditoria." />}
                {selectedDocumentId && !audit.data?.length && <EmptyState title="Nenhum acesso registrado." />}
              </div>
            </div>
          </section>
        </div>
      </section>
    </div>
  );
}
