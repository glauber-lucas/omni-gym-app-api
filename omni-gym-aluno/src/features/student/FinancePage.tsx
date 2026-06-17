import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { CheckCircle2, CreditCard, XCircle } from 'lucide-react';
import { useState } from 'react';
import type { Payment } from '@/services/api/contracts';
import { studentApi } from '@/services/api/studentApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { apiError, currency, date } from '@/shared/utils/format';

export function FinancePage() {
  const queryClient = useQueryClient();
  const invoices = useQuery({ queryKey: ['student', 'invoices'], queryFn: studentApi.invoices });
  const [payment, setPayment] = useState<Payment | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  const processPayment = useMutation({
    mutationFn: (faturaId: number) => studentApi.processPayment(faturaId, { provedor: 'SIMULADO', metodo: 'PIX' }),
    onSuccess: data => {
      setPayment(data);
      setMessage('Pagamento iniciado no gateway simulado.');
    },
    onError: error => setMessage(apiError(error))
  });

  const settle = useMutation({
    mutationFn: async ({ id, action }: { id: number; action: 'confirm' | 'reject' }) => {
      if (action === 'confirm') await studentApi.confirmPayment(id);
      else await studentApi.rejectPayment(id);
    },
    onSuccess: async () => {
      setPayment(null);
      setMessage('Status de pagamento atualizado.');
      await queryClient.invalidateQueries({ queryKey: ['student', 'invoices'] });
    },
    onError: error => setMessage(apiError(error))
  });

  return (
    <div className="page-shell">
      <header className="glass-panel">
        <p className="muted">Financeiro</p>
        <div className="mt-2 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="text-3xl font-black text-ink-100">Faturas e pagamentos</h2>
            <p className="muted mt-2 max-w-2xl">Acompanhe valores em aberto e simule pagamentos sem sair do portal.</p>
          </div>
          <span className="badge bg-primary-10 text-primary-100">{invoices.data?.length ?? 0} faturas</span>
        </div>
      </header>

      {message && <div className="status-banner">{message}</div>}

      {payment && (
        <section className="hero-panel flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-sm font-black text-white/70">Pagamento ativo</p>
            <h3 className="mt-1 text-2xl font-black">Transação #{payment.id}</h3>
            <p className="mt-1 text-sm text-white/75">Status: {payment.status} · Provedor: {payment.provedor}</p>
          </div>
          <div className="flex flex-col gap-2 sm:flex-row">
            <Button variant="secondary" onClick={() => settle.mutate({ id: payment.id, action: 'confirm' })}>
              <CheckCircle2 size={16} />
              Confirmar
            </Button>
            <Button variant="danger" onClick={() => settle.mutate({ id: payment.id, action: 'reject' })}>
              <XCircle size={16} />
              Recusar
            </Button>
          </div>
        </section>
      )}

      <section className="panel">
        <div className="hidden overflow-x-auto md:block">
          <table className="data-table">
            <thead>
              <tr>
                <th>Fatura</th>
                <th>Vencimento</th>
                <th>Status</th>
                <th>Valor</th>
                <th>Ação</th>
              </tr>
            </thead>
            <tbody>
              {(invoices.data ?? []).map(invoice => (
                <tr key={invoice.id}>
                  <td className="font-black text-ink-100">{invoice.planoNome ?? `Fatura ${invoice.id}`}</td>
                  <td>{date(invoice.dataVencimento)}</td>
                  <td><span className="badge bg-primary-10 text-primary-100">{invoice.status ?? 'Aberta'}</span></td>
                  <td className="font-black text-ink-100">{currency(invoice.valorCobrado ?? invoice.valorOriginal)}</td>
                  <td>
                    <Button variant="ghost" onClick={() => processPayment.mutate(invoice.id)} disabled={invoice.status === 'PAGA'}>
                      <CreditCard size={16} />
                      Pagar
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="space-y-3 md:hidden">
          {(invoices.data ?? []).map(invoice => (
            <div key={invoice.id} className="soft-card">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="font-black text-ink-100">{invoice.planoNome ?? `Fatura ${invoice.id}`}</p>
                  <p className="muted">{date(invoice.dataVencimento)}</p>
                </div>
                <span className="badge bg-primary-10 text-primary-100">{invoice.status ?? 'Aberta'}</span>
              </div>
              <p className="mt-3 text-xl font-black text-ink-100">{currency(invoice.valorCobrado ?? invoice.valorOriginal)}</p>
              <Button className="mt-3 w-full justify-center" variant="ghost" onClick={() => processPayment.mutate(invoice.id)} disabled={invoice.status === 'PAGA'}>
                <CreditCard size={16} />
                Pagar
              </Button>
            </div>
          ))}
        </div>

        {!invoices.data?.length && <EmptyState title="Nenhuma fatura encontrada." />}
      </section>
    </div>
  );
}
