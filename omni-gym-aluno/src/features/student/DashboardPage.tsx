import { useQueries } from '@tanstack/react-query';
import { Activity, ArrowRight, ClipboardCheck, CreditCard, FileText, ShieldCheck } from 'lucide-react';
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
      <section className="grid gap-5 lg:grid-cols-[1.35fr_0.65fr]">
        <div className="hero-panel min-h-72">
          <p className="inline-flex rounded-full bg-white/14 px-4 py-2 text-sm font-black backdrop-blur">Hoje na Omni Gym</p>
          <h2 className="mt-5 max-w-2xl text-4xl font-black leading-tight sm:text-5xl">Treino adaptado e rotina em dia.</h2>
          <p className="mt-4 max-w-2xl text-sm leading-6 text-white/75">
            Veja sua ficha diária, mantenha a matrícula atualizada e acompanhe seus compromissos financeiros com menos ruído.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link className="inline-flex items-center gap-2 rounded-2xl bg-white px-4 py-3 text-sm font-black text-primary-100 shadow-sm transition hover:-translate-y-0.5" to="/treino">
              Ver treino <ArrowRight size={16} />
            </Link>
            <Link className="inline-flex items-center gap-2 rounded-2xl border border-white/18 bg-white/12 px-4 py-3 text-sm font-black text-white backdrop-blur transition hover:-translate-y-0.5" to="/financeiro">
              Financeiro
            </Link>
          </div>
        </div>

        <div className="glass-panel flex flex-col justify-between">
          <div>
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-primary-10 text-primary-100">
              <ShieldCheck size={22} />
            </div>
            <p className="mt-5 text-sm font-bold text-ink-60">Status da matrícula</p>
            <p className="mt-2 text-3xl font-black text-primary-100">{enrollment.data?.statusMatricula ?? 'Não enviada'}</p>
            <p className="muted mt-3">Mantenha seus dados atualizados para uma prescrição mais segura.</p>
          </div>
          <Link className="mt-6 inline-flex items-center gap-2 text-sm font-black text-primary-100 hover:underline" to="/matricula">
            Atualizar dados <ArrowRight size={16} />
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
            <Link className="inline-flex items-center gap-1 text-sm font-black text-primary-100 hover:underline" to="/treino">
              Ver ficha <ArrowRight size={15} />
            </Link>
          </div>
          <div className="space-y-3">
            {(workout.data?.exercicios ?? []).slice(0, 4).map(item => (
              <div key={`${item.exercicioId}-${item.ordemExecucao}`} className="soft-card flex items-start gap-3">
                <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-2xl bg-primary-100 text-sm font-black text-white">
                  {item.ordemExecucao}
                </span>
                <div>
                  <p className="font-black text-ink-100">{item.exercicioNome ?? item.nomeExercicio ?? `Exercício ${item.exercicioId}`}</p>
                  <p className="muted">
                    {item.series} séries x {item.repeticoes} reps · {item.estacaoTrabalho ?? 'Estação livre'}
                  </p>
                </div>
              </div>
            ))}
            {!workout.data?.exercicios?.length && <p className="muted">Nenhum treino ativo encontrado.</p>}
          </div>
        </div>

        <div className="panel">
          <div className="mb-4 flex items-center justify-between">
            <h3 className="section-title">Faturas recentes</h3>
            <Link className="inline-flex items-center gap-1 text-sm font-black text-primary-100 hover:underline" to="/financeiro">
              Abrir financeiro <ArrowRight size={15} />
            </Link>
          </div>
          <div className="space-y-3">
            {invoiceList.slice(0, 4).map(item => (
              <div key={item.id} className="soft-card flex items-center justify-between gap-4">
                <div>
                  <p className="font-black text-ink-100">{item.planoNome ?? `Fatura ${item.id}`}</p>
                  <p className="muted">{item.status ?? 'Sem status'}</p>
                </div>
                <span className="rounded-2xl bg-white px-3 py-2 text-sm font-black text-ink-100 shadow-sm">{currency(item.valorCobrado ?? item.valorOriginal)}</span>
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
    <div className="metric-card">
      <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-primary-10 text-primary-100">
        <Icon size={22} />
      </div>
      <p className="muted">{label}</p>
      <p className="mt-1 text-2xl font-black text-ink-100">{value}</p>
    </div>
  );
}
