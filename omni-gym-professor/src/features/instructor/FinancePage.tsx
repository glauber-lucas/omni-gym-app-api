import { useMutation, useQueries, useQueryClient } from '@tanstack/react-query';
import { BadgeDollarSign, CheckCircle2, Percent, Plus } from 'lucide-react';
import { useState } from 'react';
import { instructorApi } from '@/services/api/instructorApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { Field } from '@/shared/components/Field';
import { apiError, currency, date } from '@/shared/utils/format';

export function FinancePage() {
  const queryClient = useQueryClient();
  const [plans, invoices, students, report] = useQueries({
    queries: [
      { queryKey: ['instructor', 'plans'], queryFn: instructorApi.plans },
      { queryKey: ['instructor', 'invoices'], queryFn: () => instructorApi.invoices() },
      { queryKey: ['instructor', 'students'], queryFn: instructorApi.students },
      { queryKey: ['instructor', 'revenue-report'], queryFn: instructorApi.revenueReport, retry: false }
    ]
  });
  const [message, setMessage] = useState<string | null>(null);
  const [plan, setPlan] = useState({ nome: '', valor: '', duracaoMeses: '1' });
  const [invoice, setInvoice] = useState({ alunoId: '', planoId: '', valor: '', dataVencimento: '' });
  const [subscription, setSubscription] = useState({ alunoId: '', planoId: '' });
  const [discounts, setDiscounts] = useState<Record<number, string>>({});
  const [payments, setPayments] = useState<Record<number, string>>({});

  const refresh = async () => {
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'plans'] });
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'invoices'] });
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'revenue-report'] });
  };

  const createPlan = useMutation({
    mutationFn: () => instructorApi.createPlan({ nome: plan.nome, valor: Number(plan.valor), duracaoMeses: Number(plan.duracaoMeses) }),
    onSuccess: async () => {
      setMessage('Plano cadastrado.');
      setPlan({ nome: '', valor: '', duracaoMeses: '1' });
      await refresh();
    },
    onError: error => setMessage(apiError(error))
  });

  const createInvoice = useMutation({
    mutationFn: () => {
      if (!invoice.alunoId) throw new Error('Selecione um aluno.');
      return instructorApi.createInvoice(Number(invoice.alunoId), {
        planoId: invoice.planoId ? Number(invoice.planoId) : undefined,
        valor: invoice.valor ? Number(invoice.valor) : undefined,
        dataVencimento: invoice.dataVencimento
      });
    },
    onSuccess: async () => {
      setMessage('Fatura emitida.');
      setInvoice({ alunoId: '', planoId: '', valor: '', dataVencimento: '' });
      await refresh();
    },
    onError: error => setMessage(apiError(error))
  });

  const createSubscription = useMutation({
    mutationFn: () => {
      if (!subscription.alunoId || !subscription.planoId) throw new Error('Selecione aluno e plano.');
      return instructorApi.createSubscription(Number(subscription.alunoId), Number(subscription.planoId));
    },
    onSuccess: async () => {
      setMessage('Assinatura criada.');
      setSubscription({ alunoId: '', planoId: '' });
      await refresh();
    },
    onError: error => setMessage(apiError(error))
  });

  const payInvoice = useMutation({
    mutationFn: ({ id, value }: { id: number; value?: number }) => instructorApi.payInvoice(id, value),
    onSuccess: async () => {
      setMessage('Pagamento registrado.');
      await refresh();
    },
    onError: error => setMessage(apiError(error))
  });

  const discountInvoice = useMutation({
    mutationFn: ({ id, desconto }: { id: number; desconto: number }) => instructorApi.discountInvoice(id, desconto),
    onSuccess: async () => {
      setMessage('Desconto aplicado.');
      await refresh();
    },
    onError: error => setMessage(apiError(error))
  });

  return (
    <div className="page-shell">
      <header className="glass-panel">
        <p className="muted">Financeiro administrativo</p>
        <div className="mt-2 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="text-3xl font-black text-ink-100">Planos, faturas e assinaturas</h2>
            <p className="muted mt-2 max-w-2xl">Crie planos, emita cobranças e acompanhe recebimentos com visão operacional.</p>
          </div>
          <span className="badge bg-primary-10 text-primary-100">{invoices.data?.length ?? 0} faturas</span>
        </div>
      </header>

      {message && <div className="status-banner">{message}</div>}

      <section className="grid gap-4 md:grid-cols-3">
        <Metric label="Recebido" value={currency(report.data?.totalRecebido)} />
        <Metric label="Pendente" value={currency(report.data?.totalPendente)} />
        <Metric label="Atrasado" value={currency(report.data?.totalAtrasado)} />
      </section>

      <section className="grid gap-5 xl:grid-cols-3">
        <form
          className="panel space-y-4"
          onSubmit={event => {
            event.preventDefault();
            createPlan.mutate();
          }}
        >
          <h3 className="section-title">Novo plano</h3>
          <Field label="Nome" value={plan.nome} onChange={event => setPlan({ ...plan, nome: event.target.value })} required />
          <Field label="Valor" type="number" step="0.01" value={plan.valor} onChange={event => setPlan({ ...plan, valor: event.target.value })} required />
          <Field label="Duração em meses" type="number" value={plan.duracaoMeses} onChange={event => setPlan({ ...plan, duracaoMeses: event.target.value })} required />
          <Button className="w-full justify-center" isLoading={createPlan.isPending}>
            <Plus size={16} />
            Criar plano
          </Button>
        </form>

        <form
          className="panel space-y-4"
          onSubmit={event => {
            event.preventDefault();
            createInvoice.mutate();
          }}
        >
          <h3 className="section-title">Emitir fatura</h3>
          <StudentSelect value={invoice.alunoId} onChange={alunoId => setInvoice({ ...invoice, alunoId })} students={students.data ?? []} />
          <PlanSelect value={invoice.planoId} onChange={planoId => setInvoice({ ...invoice, planoId })} plans={plans.data ?? []} allowEmpty />
          <Field label="Valor avulso" type="number" step="0.01" value={invoice.valor} onChange={event => setInvoice({ ...invoice, valor: event.target.value })} />
          <Field label="Vencimento" type="date" value={invoice.dataVencimento} onChange={event => setInvoice({ ...invoice, dataVencimento: event.target.value })} required />
          <Button className="w-full justify-center" isLoading={createInvoice.isPending}>
            <BadgeDollarSign size={16} />
            Emitir fatura
          </Button>
        </form>

        <form
          className="panel space-y-4"
          onSubmit={event => {
            event.preventDefault();
            createSubscription.mutate();
          }}
        >
          <h3 className="section-title">Criar assinatura</h3>
          <StudentSelect value={subscription.alunoId} onChange={alunoId => setSubscription({ ...subscription, alunoId })} students={students.data ?? []} />
          <PlanSelect value={subscription.planoId} onChange={planoId => setSubscription({ ...subscription, planoId })} plans={plans.data ?? []} />
          <Button className="w-full justify-center" isLoading={createSubscription.isPending}>
            <Plus size={16} />
            Criar assinatura
          </Button>
        </form>
      </section>

      <section className="panel">
        <h3 className="section-title">Faturas</h3>
        <div className="mt-4 hidden overflow-x-auto lg:block">
          <table className="data-table">
            <thead>
              <tr>
                <th>Aluno</th>
                <th>Plano</th>
                <th>Vencimento</th>
                <th>Status</th>
                <th>Valor</th>
                <th>Desconto</th>
                <th>Pagamento</th>
              </tr>
            </thead>
            <tbody>
              {(invoices.data ?? []).map(item => (
                <tr key={item.id}>
                  <td className="font-black text-ink-100">{item.alunoNome ?? item.alunoId}</td>
                  <td>{item.planoNome ?? 'Avulsa'}</td>
                  <td>{date(item.dataVencimento)}</td>
                  <td><span className="badge bg-primary-10 text-primary-100">{item.status ?? 'Aberta'}</span></td>
                  <td className="font-black text-ink-100">{currency(item.valorCobrado ?? item.valorOriginal)}</td>
                  <td>
                    <div className="flex gap-2">
                      <input className="input w-28" type="number" step="0.01" value={discounts[item.id] ?? ''} onChange={event => setDiscounts({ ...discounts, [item.id]: event.target.value })} />
                      <Button variant="ghost" onClick={() => discountInvoice.mutate({ id: item.id, desconto: Number(discounts[item.id] ?? 0) })}>
                        <Percent size={16} />
                      </Button>
                    </div>
                  </td>
                  <td>
                    <div className="flex gap-2">
                      <input className="input w-28" type="number" step="0.01" value={payments[item.id] ?? ''} onChange={event => setPayments({ ...payments, [item.id]: event.target.value })} />
                      <Button variant="ghost" onClick={() => payInvoice.mutate({ id: item.id, value: payments[item.id] ? Number(payments[item.id]) : undefined })}>
                        <CheckCircle2 size={16} />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="mt-4 space-y-3 lg:hidden">
          {(invoices.data ?? []).map(item => (
            <div key={item.id} className="soft-card">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="font-black text-ink-100">{item.alunoNome ?? item.alunoId}</p>
                  <p className="muted">{item.planoNome ?? 'Avulsa'} · {date(item.dataVencimento)}</p>
                </div>
                <span className="badge bg-primary-10 text-primary-100">{item.status ?? 'Aberta'}</span>
              </div>
              <p className="mt-3 text-xl font-black text-ink-100">{currency(item.valorCobrado ?? item.valorOriginal)}</p>
            </div>
          ))}
        </div>

        {!invoices.data?.length && <EmptyState title="Nenhuma fatura emitida." />}
      </section>
    </div>
  );
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div className="metric-card">
      <p className="muted">{label}</p>
      <p className="mt-1 text-2xl font-black text-ink-100">{value}</p>
    </div>
  );
}

function StudentSelect({ value, onChange, students }: { value: string; onChange: (value: string) => void; students: Array<{ id?: number; userId?: number; name?: string; username?: string }> }) {
  return (
    <label className="grid gap-1.5">
      <span className="label">Aluno</span>
      <select className="input" value={value} onChange={event => onChange(event.target.value)}>
        <option value="">Selecione</option>
        {students.map(student => (
          <option key={student.userId ?? student.id} value={student.userId ?? student.id}>
            {student.name ?? student.username}
          </option>
        ))}
      </select>
    </label>
  );
}

function PlanSelect({ value, onChange, plans, allowEmpty = false }: { value: string; onChange: (value: string) => void; plans: Array<{ id: number; nome: string }>; allowEmpty?: boolean }) {
  return (
    <label className="grid gap-1.5">
      <span className="label">Plano</span>
      <select className="input" value={value} onChange={event => onChange(event.target.value)}>
        <option value="">{allowEmpty ? 'Sem plano' : 'Selecione'}</option>
        {plans.map(plan => (
          <option key={plan.id} value={plan.id}>
            {plan.nome}
          </option>
        ))}
      </select>
    </label>
  );
}
