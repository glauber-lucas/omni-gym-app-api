import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { ClipboardCheck, HeartPulse, Save } from 'lucide-react';
import { useEffect, useState } from 'react';
import { studentApi } from '@/services/api/studentApi';
import { Button } from '@/shared/components/Button';
import { Field, Textarea } from '@/shared/components/Field';
import { apiError } from '@/shared/utils/format';

const blank = {
  telefone: '',
  endereco: '',
  contatoEmergencia: '',
  infoFamiliar: '',
  medicamentos: '',
  deficiencias: '',
  alergias: ''
};

export function EnrollmentPage() {
  const queryClient = useQueryClient();
  const { data, isLoading } = useQuery({ queryKey: ['student', 'enrollment'], queryFn: studentApi.enrollment, retry: false });
  const [form, setForm] = useState(blank);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    if (data) {
      setForm({
        telefone: data.telefone ?? '',
        endereco: data.endereco ?? '',
        contatoEmergencia: data.contatoEmergencia ?? '',
        infoFamiliar: data.infoFamiliar ?? '',
        medicamentos: data.medicamentos ?? '',
        deficiencias: data.deficiencias ?? '',
        alergias: data.alergias ?? ''
      });
    }
  }, [data]);

  const mutation = useMutation({
    mutationFn: studentApi.saveEnrollment,
    onSuccess: async () => {
      setMessage('Matrícula salva com sucesso.');
      await queryClient.invalidateQueries({ queryKey: ['student', 'enrollment'] });
    },
    onError: error => setMessage(apiError(error))
  });

  return (
    <div className="page-shell">
      <header className="glass-panel">
        <p className="muted">Matrícula & biomecânica</p>
        <div className="mt-2 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="text-3xl font-black text-ink-100">Ficha do aluno</h2>
            <p className="muted mt-2 max-w-2xl">Dados pessoais e clínicos ajudam o professor a manter seu treino mais seguro e adaptado.</p>
          </div>
          <span className="badge bg-primary-10 text-primary-100">{data?.statusMatricula ?? 'Aguardando envio'}</span>
        </div>
      </header>

      <form
        className="grid gap-5 xl:grid-cols-[1fr_340px]"
        onSubmit={event => {
          event.preventDefault();
          mutation.mutate(form);
        }}
      >
        <div className="panel grid gap-6">
          <section>
            <div className="mb-4 flex items-center gap-3">
              <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-primary-10 text-primary-100">
                <ClipboardCheck size={20} />
              </div>
              <div>
                <h3 className="section-title">Contato e endereço</h3>
                <p className="muted">Informações essenciais para matrícula e emergência.</p>
              </div>
            </div>
            <div className="grid gap-4 md:grid-cols-3">
              <Field label="Telefone" value={form.telefone} onChange={event => setForm({ ...form, telefone: event.target.value })} required />
              <Field
                label="Contato de emergência"
                value={form.contatoEmergencia}
                onChange={event => setForm({ ...form, contatoEmergencia: event.target.value })}
                required
              />
              <Field label="Status" value={data?.statusMatricula ?? 'Aguardando envio'} disabled />
            </div>
            <div className="mt-4">
              <Field label="Endereço" value={form.endereco} onChange={event => setForm({ ...form, endereco: event.target.value })} required />
            </div>
          </section>

          <section>
            <div className="mb-4 flex items-center gap-3">
              <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-secondary-20 text-secondary-100">
                <HeartPulse size={20} />
              </div>
              <div>
                <h3 className="section-title">Condição e cuidados</h3>
                <p className="muted">Preencha o que pode influenciar ajustes no treino.</p>
              </div>
            </div>
            <div className="grid gap-4 lg:grid-cols-2">
              <Textarea label="Informações familiares" value={form.infoFamiliar} onChange={event => setForm({ ...form, infoFamiliar: event.target.value })} />
              <Textarea label="Medicamentos" value={form.medicamentos} onChange={event => setForm({ ...form, medicamentos: event.target.value })} />
              <Textarea label="Deficiências" value={form.deficiencias} onChange={event => setForm({ ...form, deficiencias: event.target.value })} />
              <Textarea label="Alergias" value={form.alergias} onChange={event => setForm({ ...form, alergias: event.target.value })} />
            </div>
          </section>

          {message && <div className="status-banner">{message}</div>}

          <Button className="w-full justify-center sm:w-fit" isLoading={mutation.isPending || isLoading}>
            <Save size={16} />
            Salvar matrícula
          </Button>
        </div>

        <aside className="glass-panel h-fit">
          <p className="text-sm font-black text-ink-100">Perfil biomecânico</p>
          <p className="muted mt-2">Resumo homologado pelo professor após análise da matrícula.</p>
          <div className="mt-5 space-y-3">
            <div className="soft-card">
              <p className="text-xs font-black uppercase tracking-wide text-slate-400">Estabilidade</p>
              <p className="mt-1 font-black text-ink-100">{data?.estabilidadeTronco ?? 'Não mapeada'}</p>
            </div>
            <div className="soft-card">
              <p className="text-xs font-black uppercase tracking-wide text-slate-400">Restrições</p>
              <p className="mt-1 text-sm font-bold text-ink-80">{data?.restricoes?.length ? data.restricoes.join(', ') : 'Nenhuma registrada'}</p>
            </div>
            {data?.bloqueioMedico ? (
              <div className="rounded-2xl bg-rose-50 p-4 text-sm font-black text-rose-700">Bloqueio médico ativo</div>
            ) : (
              <div className="rounded-2xl bg-emerald-50 p-4 text-sm font-black text-emerald-700">Sem bloqueio médico ativo</div>
            )}
          </div>
        </aside>
      </form>
    </div>
  );
}
