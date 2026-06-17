import { InputHTMLAttributes, TextareaHTMLAttributes } from 'react';
import { cx } from '@/shared/utils/cx';

type FieldProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  error?: string;
};

type TextareaProps = TextareaHTMLAttributes<HTMLTextAreaElement> & {
  label: string;
  error?: string;
};

export function Field({ label, error, ...props }: FieldProps) {
  const { className, ...inputProps } = props;

  return (
    <label className="grid gap-1.5">
      <span className="label">{label}</span>
      <input className={cx('input', className)} {...inputProps} />
      {error && <span className="text-xs font-medium text-rose-600">{error}</span>}
    </label>
  );
}

export function Textarea({ label, error, ...props }: TextareaProps) {
  const { className, ...textareaProps } = props;

  return (
    <label className="grid gap-1.5">
      <span className="label">{label}</span>
      <textarea className={cx('input min-h-32 resize-y', className)} {...textareaProps} />
      {error && <span className="text-xs font-medium text-rose-600">{error}</span>}
    </label>
  );
}
