import { Activity, CreditCard, Dumbbell, FileHeart, Home, LogOut, Menu, Users, X } from 'lucide-react';
import { useState } from 'react';
import { NavLink, Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/features/auth/AuthProvider';
import { Button } from '@/shared/components/Button';
import { cx } from '@/shared/utils/cx';

const navigation = [
  { to: '/', label: 'Dashboard', icon: Home },
  { to: '/alunos', label: 'Alunos', icon: Users },
  { to: '/catalogo', label: 'Catálogo', icon: Dumbbell },
  { to: '/treinos', label: 'Treinos', icon: Activity },
  { to: '/clinico', label: 'Clínico', icon: FileHeart },
  { to: '/financeiro', label: 'Financeiro', icon: CreditCard }
];

export function AppLayout() {
  const { user, isReady, logout } = useAuth();
  const [open, setOpen] = useState(false);

  if (!isReady) {
    return <div className="grid min-h-screen place-items-center text-slate-600">Carregando...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="min-h-screen text-ink-100">
      <header className="sticky top-0 z-30 border-b border-white/70 bg-white/90 shadow-sm backdrop-blur-xl xl:hidden">
        <div className="flex h-16 items-center justify-between px-4">
          <div className="flex items-center gap-3">
            <img src="/logo.png" alt="Omni Gym Professor" className="h-11 w-11 rounded-2xl object-cover shadow-sm" />
            <div>
              <span className="block text-sm font-black leading-tight">Omni Gym</span>
              <span className="block text-xs font-bold text-primary-100">Portal do professor</span>
            </div>
          </div>
          <button className="icon-button" onClick={() => setOpen(value => !value)} aria-label="Abrir menu">
            {open ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
      </header>

      <aside
        className={cx(
          'fixed inset-y-0 left-0 z-40 w-80 border-r border-white/70 bg-white/90 p-5 shadow-soft backdrop-blur-xl transition-transform xl:translate-x-0',
          open ? 'translate-x-0' : '-translate-x-full'
        )}
      >
        <div className="flex h-full flex-col">
          <div className="mb-8 rounded-[1.5rem] border border-primary-20 bg-gradient-to-br from-white to-primary-10 p-4 shadow-card">
            <img src="/logo.png" alt="Omni Gym Professor" className="mx-auto h-24 w-24 rounded-[1.35rem] object-cover shadow-sm" />
            <div>
              <p className="mt-4 text-center text-xs font-black uppercase tracking-wide text-primary-100">Portal do professor</p>
              <h1 className="text-center text-xl font-black">Omni Gym</h1>
            </div>
          </div>

          <nav className="space-y-2.5">
            {navigation.map(item => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.to === '/'}
                onClick={() => setOpen(false)}
                className={({ isActive }) =>
                  cx(
                    'flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-black transition',
                    isActive
                      ? 'bg-gradient-to-r from-primary-100 to-secondary-100 text-white shadow-card'
                      : 'text-ink-60 hover:-translate-y-0.5 hover:bg-white hover:text-ink-100 hover:shadow-sm'
                  )
                }
              >
                <item.icon size={18} />
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="mt-auto rounded-[1.5rem] border border-slate-100 bg-slate-50/90 p-4">
            <p className="text-xs font-black uppercase tracking-wide text-slate-400">Instrutor ativo</p>
            <p className="mt-1 truncate text-sm font-black">{user.name ?? user.email}</p>
            <p className="mt-1 text-xs font-semibold text-ink-60">Alunos, treinos e financeiro com contexto.</p>
            <Button variant="ghost" className="mt-4 w-full justify-center" onClick={logout}>
              <LogOut size={16} />
              Sair
            </Button>
          </div>
        </div>
      </aside>

      {open && <button className="fixed inset-0 z-30 bg-slate-950/30 xl:hidden" onClick={() => setOpen(false)} />}

      <main className="px-4 py-6 sm:px-6 xl:ml-80 xl:px-10 xl:py-8">
        <Outlet />
      </main>
    </div>
  );
}
