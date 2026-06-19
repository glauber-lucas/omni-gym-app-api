import { useMutation } from '@tanstack/react-query';
import { Download, Eye, FileText, FileUp, Trash2, X } from 'lucide-react';
import { useEffect, useState } from 'react';
import type { MedicalDocument } from '@/services/api/contracts';
import { studentApi } from '@/services/api/studentApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { Field, Textarea } from '@/shared/components/Field';
import { apiError, date } from '@/shared/utils/format';

const documentTypes = ['LAUDO_MEDICO', 'EXAME_DIAGNOSTICO', 'PARECER_CLINICO', 'RELATORIO_FISIOTERAPIA', 'RECEITA_MEDICA', 'ATESTADO', 'OUTRO'];

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

export function DocumentsPage() {
  const [documents, setDocuments] = useState<MedicalDocument[]>([]);
  const [tipo, setTipo] = useState(documentTypes[0]);
  const [descricao, setDescricao] = useState('');
  const [dataProximaReavaliacao, setDataProximaReavaliacao] = useState('');
  const [arquivo, setArquivo] = useState<File | null>(null);
  const [preview, setPreview] = useState<DocumentPreview | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    return () => {
      if (preview?.objectUrl) URL.revokeObjectURL(preview.objectUrl);
    };
  }, [preview?.objectUrl]);

  const upload = useMutation({
    mutationFn: studentApi.uploadDocument,
    onSuccess: document => {
      setDocuments(current => [document, ...current]);
      setDescricao('');
      setArquivo(null);
      setMessage('Documento enviado com sucesso.');
    },
    onError: error => setMessage(apiError(error))
  });

  const remove = useMutation({
    mutationFn: studentApi.deleteDocument,
    onSuccess: (_, id) => {
      setDocuments(current => current.filter(item => item.id !== id));
      setMessage('Documento removido.');
    },
    onError: error => setMessage(apiError(error))
  });

  const previewDocument = useMutation({
    mutationFn: async (documento: MedicalDocument) => {
      if (!canPreviewDocument(documento.mimeType)) {
        throw new Error('Pré-visualização disponível apenas para PDF e imagens.');
      }

      const blob = await studentApi.documentFile(documento.id);
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
      blob: await studentApi.documentFile(documento.id)
    }),
    onSuccess: ({ blob, documento }) => saveBlob(blob, documento),
    onError: error => setMessage(apiError(error))
  });

  return (
    <div className="page-shell">
      <header className="glass-panel">
        <p className="muted">Clínico</p>
        <div className="mt-2 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="text-3xl font-black text-ink-100">Documentos médicos</h2>
            <p className="muted mt-2 max-w-2xl">Envie laudos, exames e pareceres para manter seu acompanhamento clínico atualizado.</p>
          </div>
          <span className="badge bg-primary-10 text-primary-100">{documents.length} nesta sessão</span>
        </div>
      </header>

      <section className="grid gap-5 xl:grid-cols-[420px_1fr]">
        <form
          className="glass-panel h-fit space-y-4"
          onSubmit={event => {
            event.preventDefault();
            if (!arquivo) {
              setMessage('Selecione um arquivo para enviar.');
              return;
            }
            upload.mutate({ arquivo, tipo, descricao, dataProximaReavaliacao: dataProximaReavaliacao ? `${dataProximaReavaliacao}T00:00:00` : undefined });
          }}
        >
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-primary-10 text-primary-100">
              <FileUp size={20} />
            </div>
            <div>
              <h3 className="section-title">Novo upload</h3>
              <p className="muted">Arquivos ficam disponíveis para análise do professor.</p>
            </div>
          </div>
          <label className="grid gap-1.5">
            <span className="label">Tipo</span>
            <select className="input" value={tipo} onChange={event => setTipo(event.target.value)}>
              {documentTypes.map(item => (
                <option key={item} value={item}>
                  {item.replaceAll('_', ' ')}
                </option>
              ))}
            </select>
          </label>
          <Field label="Próxima reavaliação" type="date" value={dataProximaReavaliacao} onChange={event => setDataProximaReavaliacao(event.target.value)} />
          <Textarea label="Descrição" value={descricao} onChange={event => setDescricao(event.target.value)} />
          <Field label="Arquivo" type="file" onChange={event => setArquivo(event.target.files?.[0] ?? null)} />
          {message && <div className="status-banner">{message}</div>}
          <Button className="w-full justify-center" isLoading={upload.isPending}>
            <FileUp size={16} />
            Enviar documento
          </Button>
        </form>

        <div className="panel">
          <div className="mb-5 flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-secondary-20 text-secondary-100">
              <FileText size={20} />
            </div>
            <div>
              <h3 className="section-title">Documentos enviados nesta sessão</h3>
              <p className="muted">Novos uploads aparecem aqui imediatamente para download ou exclusão.</p>
            </div>
          </div>
          <div className="mt-5 space-y-3">
            {documents.map(document => (
              <div key={document.id} className="soft-card flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <p className="font-black text-ink-100">{document.tipo.replaceAll('_', ' ')}</p>
                  <p className="muted">
                    {document.descricao || 'Sem descrição'} · Upload em {date(document.dataUpload)}
                  </p>
                </div>
                <div className="flex gap-2">
                  <button className="icon-button" onClick={() => previewDocument.mutate(document)} aria-label="Visualizar documento" disabled={previewDocument.isPending}>
                    <Eye size={16} />
                  </button>
                  <button className="icon-button" onClick={() => downloadDocument.mutate(document)} aria-label="Baixar documento" disabled={downloadDocument.isPending}>
                    <Download size={16} />
                  </button>
                  <button className="icon-button" onClick={() => remove.mutate(document.id)} aria-label="Excluir documento">
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            ))}
            {!documents.length && <EmptyState title="Nenhum documento enviado nesta sessão." />}
          </div>
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
