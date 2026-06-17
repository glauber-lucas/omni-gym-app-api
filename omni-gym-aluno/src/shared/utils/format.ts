import { format } from 'date-fns';

export function currency(value?: number | string | null) {
  const numeric = Number(value ?? 0);
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(numeric);
}

export function date(value?: string | Date | null) {
  if (!value) return 'Sem data';
  return format(new Date(value), 'dd/MM/yyyy');
}

export function apiError(error: unknown) {
  const anyError = error as { response?: { data?: any }; message?: string };
  const data = anyError.response?.data;

  if (typeof data === 'string') return data;
  if (data?.error) return data.error;
  if (data && typeof data === 'object') return Object.values(data).join(' ');

  return anyError.message ?? 'Não foi possível concluir a operação.';
}
