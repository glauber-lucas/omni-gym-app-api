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
      <header>
        <p className="muted">Financeiro</p>
        <h2 className="text-3xl font-black">Faturas e pagamentos</h2>
      </header>

      {message && <div className="panel border-primary-20 bg-primary-10 text-sm font-semibold text-slate-700">{message}</div>}

      {payment && (
        <section className="panel flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="muted">Pagamento ativo</p>
            <h3 className="section-title">Transação #{payment.id}</h3>
            <p className="muted">Status: {payment.status} · Provedor: {payment.provedor}</p>
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
                  <td>{invoice.planoNome ?? `Fatura ${invoice.id}`}</td>
                  <td>{date(invoice.dataVencimento)}</td>
                  <td><span className="badge bg-slate-100 text-slate-700">{invoice.status ?? 'Aberta'}</span></td>
                  <td className="font-bold">{currency(invoice.valorCobrado ?? invoice.valorOriginal)}</td>
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
            <div key={invoice.id} className="rounded-lg bg-slate-50 p-4">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="font-bold">{invoice.planoNome ?? `Fatura ${invoice.id}`}</p>
                  <p className="muted">{date(invoice.dataVencimento)}</p>
                </div>
                <span className="badge bg-slate-100 text-slate-700">{invoice.status ?? 'Aberta'}</span>
              </div>
              <p className="mt-3 text-xl font-black">{currency(invoice.valorCobrado ?? invoice.valorOriginal)}</p>
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
