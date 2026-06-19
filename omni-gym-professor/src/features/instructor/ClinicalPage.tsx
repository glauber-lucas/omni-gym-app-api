import { useMutation, useQueries, useQuery, useQueryClient } from '@tanstack/react-query';
import { Download, Eye, FilePlus2, FileText, Trash2, X } from 'lucide-react';
import { useEffect, useState } from 'react';
import type { MedicalDocument } from '@/services/api/contracts';
import { instructorApi } from '@/services/api/instructorApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { Field, Textarea } from '@/shared/components/Field';
import { apiError, date } from '@/shared/utils/format';

const documentTypes = ['', 'LAUDO_MEDICO', 'EXAME_DIAGNOSTICO', 'PARECER_CLINICO', 'RELATORIO_FISIOTERAPIA', 'RECEITA_MEDICA', 'ATESTADO', 'OUTRO'];

type DocumentPreview = {
  title: string;
  objectUrl: string;
  mimeType: string;
};

function canPreviewDocument(mimeType?: string) {
  return Boolean(mimeType && (mimeType === 'application/pdf' || mimeType.startsWith('image/')));
}

function documentTitle(documento: MedicalDocument) {
  return `${documento.tipo.replaceAll('_', ' ')} #${documento.id}`;
}

function extensionFromMimeType(mimeType?: string) {
  if (mimeType === 'application/pdf') return '.pdf';
  if (mimeType === 'image/jpeg') return '.jpg';
  if (mimeType === 'image/png') return '.png';
  if (mimeType === 'image/tiff') return '.tiff';
  if (mimeType === 'application/msword') return '.doc';
  if (mimeType === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') return '.docx';
  return '';
}

function saveBlob(blob: Blob, documento: MedicalDocument) {
  const objectUrl = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = objectUrl;
  link.download = `documento-${documento.id}${extensionFromMimeType(documento.mimeType || blob.type)}`;
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(objectUrl);
}

export function ClinicalPage() {
  const queryClient = useQueryClient();
  const [students] = useQueries({
    queries: [{ queryKey: ['instructor', 'students'], queryFn: instructorApi.students }]
  });
  const [alunoId, setAlunoId] = useState('');
  const [tipo, setTipo] = useState('');
  const [selectedDocumentId, setSelectedDocumentId] = useState<number | null>(null);
  const [preview, setPreview] = useState<DocumentPreview | null>(null);
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

  useEffect(() => {
    return () => {
      if (preview?.objectUrl) URL.revokeObjectURL(preview.objectUrl);
    };
  }, [preview?.objectUrl]);

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

  const previewDocument = useMutation({
    mutationFn: async (documento: MedicalDocument) => {
      if (!canPreviewDocument(documento.mimeType)) {
        throw new Error('Pré-visualização disponível apenas para PDF e imagens.');
      }

      const blob = await instructorApi.documentFile(documento.id);
      const mimeType = blob.type || documento.mimeType || '';

      if (!canPreviewDocument(mimeType)) {
        throw new Error('Pré-visualização disponível apenas para PDF e imagens.');
      }

      return {
        title: documentTitle(documento),
        objectUrl: URL.createObjectURL(blob),
        mimeType
      };
    },
    onSuccess: nextPreview => setPreview(nextPreview),
    onError: error => setMessage(apiError(error))
  });

  const downloadDocument = useMutation({
    mutationFn: async (documento: MedicalDocument) => ({
      documento,
      blob: await instructorApi.documentFile(documento.id)
    }),
    onSuccess: ({ blob, documento }) => saveBlob(blob, documento),
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
                    <button className="icon-button" onClick={() => previewDocument.mutate(document)} aria-label="Visualizar documento" disabled={previewDocument.isPending}>
                      <Eye size={16} />
                    </button>
                    <button className="icon-button" onClick={() => downloadDocument.mutate(document)} aria-label="Baixar documento" disabled={downloadDocument.isPending}>
                      <Download size={16} />
                    </button>
                    <button className="icon-button" onClick={() => setSelectedDocumentId(document.id)} aria-label="Ver auditoria">
                      <FileText size={16} />
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
      <DocumentPreviewDialog preview={preview} onClose={() => setPreview(null)} />
    </div>
  );
}

function DocumentPreviewDialog({ preview, onClose }: { preview: DocumentPreview | null; onClose: () => void }) {
  if (!preview) return null;

  const isPdf = preview.mimeType === 'application/pdf';
  const isImage = preview.mimeType.startsWith('image/');

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/70 p-4 backdrop-blur-sm" role="dialog" aria-modal="true">
      <div className="w-full max-w-5xl rounded-[1.75rem] border border-white/70 bg-white p-4 shadow-soft">
        <div className="mb-3 flex items-center justify-between gap-3">
          <h3 className="section-title">{preview.title}</h3>
          <button className="icon-button" type="button" onClick={onClose} aria-label="Fechar visualização">
            <X size={16} />
          </button>
        </div>
        <div className="flex h-[72vh] min-h-80 items-center justify-center overflow-hidden rounded-2xl bg-slate-100">
          {isPdf && <iframe className="h-full w-full" src={preview.objectUrl} title={preview.title} />}
          {isImage && <img className="max-h-full w-full object-contain" src={preview.objectUrl} alt={preview.title} />}
          {!isPdf && !isImage && <p className="muted p-6">Este formato não possui pré-visualização no navegador.</p>}
        </div>
      </div>
    </div>
  );
}
