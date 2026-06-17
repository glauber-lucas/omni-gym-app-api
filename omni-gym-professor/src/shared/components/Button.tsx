import { ButtonHTMLAttributes } from 'react';
import { cx } from '@/shared/utils/cx';

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger';
  isLoading?: boolean;
};

const variants = {
  primary:
    'bg-gradient-to-r from-primary-100 to-secondary-100 text-white shadow-card hover:-translate-y-0.5 hover:shadow-soft focus:ring-primary-20',
  secondary:
    'bg-ink-100 text-white shadow-card hover:-translate-y-0.5 hover:bg-ink-80 hover:shadow-soft focus:ring-secondary-20',
  ghost:
    'border border-slate-200/80 bg-white/90 text-ink-60 shadow-sm hover:-translate-y-0.5 hover:border-primary-60 hover:text-ink-100 hover:shadow-card focus:ring-primary-20',
  danger:
    'bg-rose-600 text-white shadow-sm hover:-translate-y-0.5 hover:bg-rose-500 hover:shadow-card focus:ring-rose-100'
};

export function Button({ className, children, variant = 'primary', isLoading, disabled, ...props }: ButtonProps) {
  return (
    <button
      className={cx(
        'inline-flex min-h-11 items-center gap-2 rounded-2xl px-5 py-2.5 text-sm font-black transition focus:outline-none focus:ring-4 disabled:cursor-not-allowed disabled:translate-y-0 disabled:opacity-60',
        variants[variant],
        className
      )}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading ? 'Carregando...' : children}
    </button>
  );
}
