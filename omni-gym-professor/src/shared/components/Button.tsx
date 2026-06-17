import { ButtonHTMLAttributes } from 'react';
import { cx } from '@/shared/utils/cx';

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger';
  isLoading?: boolean;
};

const variants = {
  primary: 'bg-primary-100 text-slate-950 hover:bg-primary-80 focus:ring-primary-20',
  secondary: 'bg-secondary-100 text-white hover:bg-secondary-80 focus:ring-secondary-20',
  ghost: 'border border-slate-200 bg-white text-slate-700 hover:border-primary-60 hover:text-slate-950 focus:ring-primary-20',
  danger: 'bg-rose-600 text-white hover:bg-rose-500 focus:ring-rose-100'
};

export function Button({ className, children, variant = 'primary', isLoading, disabled, ...props }: ButtonProps) {
  return (
    <button
      className={cx(
        'inline-flex min-h-10 items-center gap-2 rounded-lg px-4 py-2 text-sm font-semibold transition focus:outline-none focus:ring-4 disabled:cursor-not-allowed disabled:opacity-60',
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
