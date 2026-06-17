import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import { AuthPage } from '@/features/auth/AuthPage';
import { AuthProvider } from '@/features/auth/AuthProvider';
import { CatalogPage } from '@/features/instructor/CatalogPage';
import { ClinicalPage } from '@/features/instructor/ClinicalPage';
import { DashboardPage } from '@/features/instructor/DashboardPage';
import { FinancePage } from '@/features/instructor/FinancePage';
import { StudentsPage } from '@/features/instructor/StudentsPage';
import { WorkoutsPage } from '@/features/instructor/WorkoutsPage';
import { AppLayout } from './AppLayout';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false
    }
  }
});

const router = createBrowserRouter([
  { path: '/login', element: <AuthPage mode="login" /> },
  { path: '/cadastro', element: <AuthPage mode="register" /> },
  {
    path: '/',
    element: <AppLayout />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'alunos', element: <StudentsPage /> },
      { path: 'catalogo', element: <CatalogPage /> },
      { path: 'treinos', element: <WorkoutsPage /> },
      { path: 'clinico', element: <ClinicalPage /> },
      { path: 'financeiro', element: <FinancePage /> }
    ]
  }
]);

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </QueryClientProvider>
  );
}
