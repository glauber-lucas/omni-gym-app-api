import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Save } from 'lucide-react';
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
      <header>
        <p className="muted">Matrícula & biomecânica</p>
        <h2 className="text-3xl font-black">Ficha do aluno</h2>
      </header>

      <form
        className="panel grid gap-5"
        onSubmit={event => {
          event.preventDefault();
          mutation.mutate(form);
        }}
      >
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
        <Field label="Endereço" value={form.endereco} onChange={event => setForm({ ...form, endereco: event.target.value })} required />
        <div className="grid gap-4 lg:grid-cols-2">
          <Textarea label="Informações familiares" value={form.infoFamiliar} onChange={event => setForm({ ...form, infoFamiliar: event.target.value })} />
          <Textarea label="Medicamentos" value={form.medicamentos} onChange={event => setForm({ ...form, medicamentos: event.target.value })} />
          <Textarea label="Deficiências" value={form.deficiencias} onChange={event => setForm({ ...form, deficiencias: event.target.value })} />
          <Textarea label="Alergias" value={form.alergias} onChange={event => setForm({ ...form, alergias: event.target.value })} />
        </div>

        {data?.restricoes?.length ? (
          <div className="rounded-lg bg-secondary-20 p-4">
            <p className="font-bold">Perfil biomecânico homologado</p>
            <p className="muted mt-1">
              Estabilidade: {data.estabilidadeTronco ?? 'Não mapeada'} · Restrições: {data.restricoes.join(', ')}
            </p>
          </div>
        ) : null}

        {message && <div className="rounded-lg bg-slate-50 p-3 text-sm font-semibold text-slate-700">{message}</div>}

        <Button className="w-full justify-center sm:w-fit" isLoading={mutation.isPending || isLoading}>
          <Save size={16} />
          Salvar matrícula
        </Button>
      </form>
    </div>
  );
}
