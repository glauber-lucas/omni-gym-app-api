import { ReactNode } from 'react';

export function EmptyState({ title, children }: { title: string; children?: ReactNode }) {
  return (
    <div className="rounded-[1.5rem] border border-dashed border-primary-40 bg-primary-10/70 p-7 text-center">
      <p className="font-black text-ink-80">{title}</p>
      {children && <div className="mt-2 text-sm leading-relaxed text-ink-60">{children}</div>}
    </div>
  );
}
