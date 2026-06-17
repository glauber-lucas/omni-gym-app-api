import { useQueries } from '@tanstack/react-query';
import { Activity, ClipboardCheck, CreditCard, FileText } from 'lucide-react';
import { Link } from 'react-router-dom';
import { studentApi } from '@/services/api/studentApi';
import { currency } from '@/shared/utils/format';

export function DashboardPage() {
  const [enrollment, workout, invoices] = useQueries({
    queries: [
      { queryKey: ['student', 'enrollment'], queryFn: studentApi.enrollment, retry: false },
      { queryKey: ['student', 'workout'], queryFn: studentApi.workout, retry: false },
      { queryKey: ['student', 'invoices'], queryFn: studentApi.invoices, retry: false }
    ]
  });

  const invoiceList = invoices.data ?? [];
  const pendingTotal = invoiceList
    .filter(item => item.status !== 'PAGA')
    .reduce((sum, item) => sum + Number(item.valorCobrado ?? item.valorOriginal ?? 0), 0);

  return (
    <div className="page-shell">
      <section className="grid gap-4 lg:grid-cols-[1.3fr_0.7fr]">
        <div className="panel overflow-hidden bg-slate-950 text-white">
          <div className="relative z-10">
            <p className="text-sm font-semibold text-secondary-80">Hoje na Omni Gym</p>
            <h2 className="mt-3 text-3xl font-black">Treino adaptado e rotina em dia.</h2>
            <p className="mt-3 max-w-2xl text-sm text-white/70">
              Veja sua ficha diária, mantenha a matrícula atualizada e acompanhe seus compromissos financeiros.
            </p>
          </div>
        </div>
        <div className="panel">
          <p className="muted">Status da matrícula</p>
          <p className="mt-2 text-3xl font-black text-primary-100">{enrollment.data?.statusMatricula ?? 'Não enviada'}</p>
          <Link className="mt-4 inline-flex text-sm font-bold text-primary-100 hover:underline" to="/matricula">
            Atualizar dados
          </Link>
        </div>
      </section>

      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <Metric icon={Activity} label="Exercícios do dia" value={workout.data?.exercicios?.length ?? 0} />
        <Metric icon={ClipboardCheck} label="Estabilidade" value={enrollment.data?.estabilidadeTronco ?? 'A definir'} />
        <Metric icon={CreditCard} label="Total pendente" value={currency(pendingTotal)} />
        <Metric icon={FileText} label="Documentos" value="Upload seguro" />
      </section>

      <section className="grid gap-4 lg:grid-cols-2">
        <div className="panel">
          <div className="mb-4 flex items-center justify-between">
            <h3 className="section-title">Próximo treino</h3>
            <Link className="text-sm font-bold text-primary-100 hover:underline" to="/treino">
              Ver ficha
            </Link>
          </div>
          <div className="space-y-3">
            {(workout.data?.exercicios ?? []).slice(0, 4).map(item => (
              <div key={`${item.exercicioId}-${item.ordemExecucao}`} className="rounded-lg bg-slate-50 p-3">
                <p className="font-semibold">{item.exercicioNome ?? item.nomeExercicio ?? `Exercício ${item.exercicioId}`}</p>
                <p className="muted">
                  {item.series} séries x {item.repeticoes} reps · {item.estacaoTrabalho ?? 'Estação livre'}
                </p>
              </div>
            ))}
            {!workout.data?.exercicios?.length && <p className="muted">Nenhum treino ativo encontrado.</p>}
          </div>
        </div>

        <div className="panel">
          <div className="mb-4 flex items-center justify-between">
            <h3 className="section-title">Faturas recentes</h3>
            <Link className="text-sm font-bold text-primary-100 hover:underline" to="/financeiro">
              Abrir financeiro
            </Link>
          </div>
          <div className="space-y-3">
            {invoiceList.slice(0, 4).map(item => (
              <div key={item.id} className="flex items-center justify-between rounded-lg bg-slate-50 p-3">
                <div>
                  <p className="font-semibold">{item.planoNome ?? `Fatura ${item.id}`}</p>
                  <p className="muted">{item.status ?? 'Sem status'}</p>
                </div>
                <span className="font-bold">{currency(item.valorCobrado ?? item.valorOriginal)}</span>
              </div>
            ))}
            {!invoiceList.length && <p className="muted">Nenhuma fatura emitida ainda.</p>}
          </div>
        </div>
      </section>
    </div>
  );
}

function Metric({ icon: Icon, label, value }: { icon: typeof Activity; label: string; value: string | number }) {
  return (
    <div className="panel">
      <div className="mb-4 flex h-11 w-11 items-center justify-center rounded-lg bg-primary-10 text-primary-100">
        <Icon size={21} />
      </div>
      <p className="muted">{label}</p>
      <p className="mt-1 text-xl font-black">{value}</p>
    </div>
  );
}
