import { useQuery } from '@tanstack/react-query';
import { studentApi } from '@/services/api/studentApi';
import { EmptyState } from '@/shared/components/EmptyState';
import { currency, date } from '@/shared/utils/format';

export function FinancePage() {
  const invoices = useQuery({ queryKey: ['student', 'invoices'], queryFn: studentApi.invoices });

  return (
    <div className="page-shell">
      <header className="glass-panel">
        <p className="muted">Financeiro</p>
        <div className="mt-2 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="text-3xl font-black text-ink-100">Faturas e pagamentos</h2>
            <p className="muted mt-2 max-w-2xl">Acompanhe valores em aberto e o status das suas faturas.</p>
          </div>
          <span className="badge bg-primary-10 text-primary-100">{invoices.data?.length ?? 0} faturas</span>
        </div>
      </header>

      <section className="panel">
        <div className="hidden overflow-x-auto md:block">
          <table className="data-table">
            <thead>
              <tr>
                <th>Fatura</th>
                <th>Vencimento</th>
                <th>Status</th>
                <th>Valor</th>
              </tr>
            </thead>
            <tbody>
              {(invoices.data ?? []).map(invoice => (
                <tr key={invoice.id}>
                  <td className="font-black text-ink-100">{invoice.planoNome ?? `Fatura ${invoice.id}`}</td>
                  <td>{date(invoice.dataVencimento)}</td>
                  <td><span className="badge bg-primary-10 text-primary-100">{invoice.status ?? 'Aberta'}</span></td>
                  <td className="font-black text-ink-100">{currency(invoice.valorCobrado ?? invoice.valorOriginal)}</td>
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
            </div>
          ))}
        </div>

        {!invoices.data?.length && <EmptyState title="Nenhuma fatura encontrada." />}
      </section>
    </div>
  );
}
