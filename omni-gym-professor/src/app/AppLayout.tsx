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
    <div className="min-h-screen bg-slate-50 text-slate-950">
      <header className="sticky top-0 z-30 border-b border-white/70 bg-white/90 backdrop-blur xl:hidden">
        <div className="flex h-16 items-center justify-between px-4">
          <div className="flex items-center gap-3">
            <img src="/logo.png" alt="Omni Gym Professor" className="h-10 w-10 object-contain" />
            <span className="font-semibold">Omni Gym Professor</span>
          </div>
          <button className="icon-button" onClick={() => setOpen(value => !value)} aria-label="Abrir menu">
            {open ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
      </header>

      <aside
        className={cx(
          'fixed inset-y-0 left-0 z-40 w-72 border-r border-white/70 bg-white/95 p-5 shadow-soft backdrop-blur transition-transform xl:translate-x-0',
          open ? 'translate-x-0' : '-translate-x-full'
        )}
      >
        <div className="flex h-full flex-col">
          <div className="mb-8 flex items-center gap-3">
            <img src="/logo.png" alt="Omni Gym Professor" className="h-14 w-14 object-contain" />
            <div>
              <p className="text-sm font-semibold text-primary-100">Portal do professor</p>
              <h1 className="text-xl font-bold">Omni Gym</h1>
            </div>
          </div>

          <nav className="space-y-2">
            {navigation.map(item => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.to === '/'}
                onClick={() => setOpen(false)}
                className={({ isActive }) =>
                  cx(
                    'flex items-center gap-3 rounded-lg px-3 py-3 text-sm font-semibold transition',
                    isActive
                      ? 'bg-primary-20 text-slate-950 shadow-sm'
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-950'
                  )
                }
              >
                <item.icon size={18} />
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="mt-auto rounded-lg bg-slate-50 p-4">
            <p className="text-xs uppercase tracking-wide text-slate-400">Instrutor</p>
            <p className="mt-1 truncate text-sm font-semibold">{user.name ?? user.email}</p>
            <Button variant="ghost" className="mt-4 w-full justify-center" onClick={logout}>
              <LogOut size={16} />
              Sair
            </Button>
          </div>
        </div>
      </aside>

      {open && <button className="fixed inset-0 z-30 bg-slate-950/30 xl:hidden" onClick={() => setOpen(false)} />}

      <main className="px-4 py-6 sm:px-6 xl:ml-72 xl:px-10 xl:py-8">
        <Outlet />
      </main>
    </div>
  );
}
