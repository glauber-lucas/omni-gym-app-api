import { ReactNode } from 'react';

export function EmptyState({ title, children }: { title: string; children?: ReactNode }) {
  return (
    <div className="rounded-lg border border-dashed border-slate-200 bg-slate-50 p-6 text-center">
      <p className="font-semibold text-slate-700">{title}</p>
      {children && <div className="mt-2 text-sm text-slate-500">{children}</div>}
    </div>
  );
}
