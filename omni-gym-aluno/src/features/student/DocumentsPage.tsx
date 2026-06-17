import { useMutation } from '@tanstack/react-query';
import { Download, FileText, FileUp, Trash2 } from 'lucide-react';
import { useState } from 'react';
import type { MedicalDocument } from '@/services/api/contracts';
import { studentApi } from '@/services/api/studentApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { Field, Textarea } from '@/shared/components/Field';
import { apiError, date } from '@/shared/utils/format';

const documentTypes = ['LAUDO_MEDICO', 'EXAME_DIAGNOSTICO', 'PARECER_CLINICO', 'RELATORIO_FISIOTERAPIA', 'RECEITA_MEDICA', 'ATESTADO', 'OUTRO'];

export function DocumentsPage() {
  const [documents, setDocuments] = useState<MedicalDocument[]>([]);
  const [tipo, setTipo] = useState(documentTypes[0]);
  const [descricao, setDescricao] = useState('');
  const [dataProximaReavaliacao, setDataProximaReavaliacao] = useState('');
  const [arquivo, setArquivo] = useState<File | null>(null);
  const [message, setMessage] = useState<string | null>(null);

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
                  <a className="icon-button" href={studentApi.documentDownloadUrl(document.id)} target="_blank" rel="noreferrer" aria-label="Baixar documento">
                    <Download size={16} />
                  </a>
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
    </div>
  );
}
