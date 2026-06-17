import { useQueries } from '@tanstack/react-query';
import { Activity, AlertTriangle, CreditCard, Users } from 'lucide-react';
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
      <section className="grid gap-4 lg:grid-cols-[1.3fr_0.7fr]">
        <div className="panel overflow-hidden bg-slate-950 text-white">
          <p className="text-sm font-semibold text-primary-80">Centro operacional</p>
          <h2 className="mt-3 text-3xl font-black">Acompanhe alunos, treinos e faturamento sem perder contexto.</h2>
          <p className="mt-3 max-w-2xl text-sm text-white/70">
            O portal reúne homologação, acessibilidade biomecânica, catálogo, treino, clínico e financeiro no mesmo fluxo.
          </p>
        </div>
        <div className="panel">
          <p className="muted">Pendências de matrícula</p>
          <p className="mt-2 text-4xl font-black text-slate-950">{pending.data?.length ?? 0}</p>
          <Link className="mt-4 inline-flex text-sm font-bold text-slate-950 hover:underline" to="/alunos">
            Revisar agora
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
            <Link className="text-sm font-bold text-slate-950 hover:underline" to="/alunos">
              Abrir alunos
            </Link>
          </div>
          <div className="space-y-3">
            {(pending.data ?? []).slice(0, 5).map(student => (
              <div key={student.userId ?? student.id} className="rounded-lg bg-slate-50 p-3">
                <p className="font-semibold">{student.name ?? student.username}</p>
                <p className="muted">{student.telefone ?? 'Telefone não informado'} · {student.statusMatricula}</p>
              </div>
            ))}
            {!pending.data?.length && <p className="muted">Nenhuma matrícula pendente.</p>}
          </div>
        </div>

        <div className="panel">
          <div className="mb-4 flex items-center justify-between">
            <h3 className="section-title">Financeiro</h3>
            <Link className="text-sm font-bold text-slate-950 hover:underline" to="/financeiro">
              Abrir financeiro
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
    <div className="panel">
      <div className="mb-4 flex h-11 w-11 items-center justify-center rounded-lg bg-primary-20 text-slate-950">
        <Icon size={21} />
      </div>
      <p className="muted">{label}</p>
      <p className="mt-1 text-xl font-black">{value}</p>
    </div>
  );
}

function Summary({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-lg bg-slate-50 p-4">
      <p className="muted">{label}</p>
      <p className="mt-1 font-black">{value}</p>
    </div>
  );
}
