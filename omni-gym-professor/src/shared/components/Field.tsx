import { InputHTMLAttributes, TextareaHTMLAttributes } from 'react';

type FieldProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  error?: string;
};

type TextareaProps = TextareaHTMLAttributes<HTMLTextAreaElement> & {
  label: string;
  error?: string;
};

export function Field({ label, error, ...props }: FieldProps) {
  return (
    <label className="grid gap-1.5">
      <span className="label">{label}</span>
      <input className="input" {...props} />
      {error && <span className="text-xs font-medium text-rose-600">{error}</span>}
    </label>
  );
}

export function Textarea({ label, error, ...props }: TextareaProps) {
  return (
    <label className="grid gap-1.5">
      <span className="label">{label}</span>
      <textarea className="input min-h-28 resize-y" {...props} />
      {error && <span className="text-xs font-medium text-rose-600">{error}</span>}
    </label>
  );
}
