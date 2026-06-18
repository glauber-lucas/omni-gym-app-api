import { useQueryClient } from '@tanstack/react-query';
import { ReactNode, createContext, useContext, useEffect, useMemo, useState } from 'react';
import { authApi } from '@/services/api/studentApi';
import { AUTH_LOGOUT_EVENT, tokenStore } from '@/services/api/client';
import type { User } from '@/services/api/contracts';

type AuthContextValue = {
  user: User | null;
  isReady: boolean;
  login: (payload: { identifier: string; password: string }) => Promise<void>;
  register: (payload: { usuario: string; senha: string }) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);
const EXPECTED_ROLE = 'ROLE_ALUNO';

function ensureStudentAccess(user: User) {
  if (user.role !== EXPECTED_ROLE) {
    tokenStore.clear();
    throw new Error('Esta conta não tem acesso ao portal do aluno.');
  }

  return user;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const queryClient = useQueryClient();
  const [user, setUser] = useState<User | null>(null);
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    async function restore() {
      if (!tokenStore.getAccess()) {
        setIsReady(true);
        return;
      }

      try {
        setUser(ensureStudentAccess(await authApi.me()));
      } catch {
        tokenStore.clear();
      } finally {
        setIsReady(true);
      }
    }

    void restore();
  }, []);

  useEffect(() => {
    function handleLogout() {
      setUser(null);
      queryClient.clear();
    }

    window.addEventListener(AUTH_LOGOUT_EVENT, handleLogout);
    return () => window.removeEventListener(AUTH_LOGOUT_EVENT, handleLogout);
  }, [queryClient]);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isReady,
      async login(payload) {
        const response = await authApi.login(payload);
        setUser(ensureStudentAccess(response.user ?? (await authApi.me())));
      },
      async register(payload) {
        await authApi.register(payload);
      },
      logout() {
        tokenStore.clear();
        setUser(null);
        queryClient.clear();
      }
    }),
    [isReady, queryClient, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
