import { useQueries } from '@tanstack/react-query';
import { Activity, AlertTriangle, ArrowRight, CreditCard, Users } from 'lucide-react';
import { Link } from 'react-router-dom';
import { instructorApi } from '@/services/api/instructorApi';
import { currency } from '@/shared/utils/format';

export function DashboardPage() {
  const [students, pending, invoices, report] = useQueries({
    queries: [
      { queryKey: ['instructor', 'students'], queryFn: instructorApi.students },
      { queryKey: ['instructor', 'pending-students'], queryFn: instructorApi.pendingStudents },
      { queryKey: ['instructor', 'invoices'], queryFn: () => instructorApi.invoices() },
      { queryKey: ['instructor', 'revenue-report'], queryFn: instructorApi.revenueReport, retry: false }
    ]
  });

  return (
    <div className="page-shell">
      <section className="grid gap-5 lg:grid-cols-[1.35fr_0.65fr]">
        <div className="hero-panel min-h-72">
          <p className="inline-flex rounded-full bg-white/14 px-4 py-2 text-sm font-black backdrop-blur">Centro operacional</p>
          <h2 className="mt-5 max-w-3xl text-4xl font-black leading-tight sm:text-5xl">Alunos, treinos e faturamento sem perder contexto.</h2>
          <p className="mt-4 max-w-2xl text-sm leading-6 text-white/75">
            O portal reúne homologação, acessibilidade biomecânica, catálogo, treino, clínico e financeiro no mesmo fluxo.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link className="inline-flex items-center gap-2 rounded-2xl bg-white px-4 py-3 text-sm font-black text-primary-100 shadow-sm transition hover:-translate-y-0.5" to="/alunos">
              Revisar alunos <ArrowRight size={16} />
            </Link>
            <Link className="inline-flex items-center gap-2 rounded-2xl border border-white/18 bg-white/12 px-4 py-3 text-sm font-black text-white backdrop-blur transition hover:-translate-y-0.5" to="/financeiro">
              Ver financeiro
            </Link>
          </div>
        </div>
        <div className="glass-panel flex flex-col justify-between">
          <div>
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-primary-20 text-primary-100">
              <AlertTriangle size={22} />
            </div>
            <p className="mt-5 text-sm font-bold text-ink-60">Pendências de matrícula</p>
            <p className="mt-2 text-5xl font-black text-ink-100">{pending.data?.length ?? 0}</p>
            <p className="muted mt-3">Revise cadastros antes de liberar planos e treinos.</p>
          </div>
          <Link className="mt-6 inline-flex items-center gap-2 text-sm font-black text-primary-100 hover:underline" to="/alunos">
            Revisar agora <ArrowRight size={16} />
          </Link>
        </div>
      </section>

      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <Metric icon={Users} label="Alunos cadastrados" value={students.data?.length ?? 0} />
        <Metric icon={AlertTriangle} label="Matrículas pendentes" value={pending.data?.length ?? 0} />
        <Metric icon={CreditCard} label="Total pendente" value={currency(report.data?.totalPendente)} />
        <Metric icon={Activity} label="Faturas emitidas" value={invoices.data?.length ?? 0} />
      </section>

      <section className="grid gap-4 lg:grid-cols-2">
        <div className="panel">
          <div className="mb-4 flex items-center justify-between">
            <h3 className="section-title">Alunos aguardando ação</h3>
            <Link className="inline-flex items-center gap-1 text-sm font-black text-primary-100 hover:underline" to="/alunos">
              Abrir alunos <ArrowRight size={15} />
            </Link>
          </div>
          <div className="space-y-3">
            {(pending.data ?? []).slice(0, 5).map(student => (
              <div key={student.userId ?? student.id} className="soft-card">
                <p className="font-black text-ink-100">{student.name ?? student.username}</p>
                <p className="muted">
                  {student.telefone ?? 'Telefone não informado'} · {student.statusMatricula}
                </p>
              </div>
            ))}
            {!pending.data?.length && <p className="muted">Nenhuma matrícula pendente.</p>}
          </div>
        </div>

        <div className="panel">
          <div className="mb-4 flex items-center justify-between">
            <h3 className="section-title">Financeiro</h3>
            <Link className="inline-flex items-center gap-1 text-sm font-black text-primary-100 hover:underline" to="/financeiro">
              Abrir financeiro <ArrowRight size={15} />
            </Link>
          </div>
          <div className="grid gap-3 sm:grid-cols-3">
            <Summary label="Recebido" value={currency(report.data?.totalRecebido)} />
            <Summary label="Pendente" value={currency(report.data?.totalPendente)} />
            <Summary label="Atrasado" value={currency(report.data?.totalAtrasado)} />
          </div>
        </div>
      </section>
    </div>
  );
}

function Metric({ icon: Icon, label, value }: { icon: typeof Activity; label: string; value: string | number }) {
  return (
    <div className="metric-card">
      <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-primary-20 text-primary-100">
        <Icon size={22} />
      </div>
      <p className="muted">{label}</p>
      <p className="mt-1 text-2xl font-black text-ink-100">{value}</p>
    </div>
  );
}

function Summary({ label, value }: { label: string; value: string }) {
  return (
    <div className="soft-card">
      <p className="muted">{label}</p>
      <p className="mt-1 font-black text-ink-100">{value}</p>
    </div>
  );
}
