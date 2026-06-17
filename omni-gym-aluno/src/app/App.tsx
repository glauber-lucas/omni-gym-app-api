import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import { AuthProvider } from '@/features/auth/AuthProvider';
import { AuthPage } from '@/features/auth/AuthPage';
import { DashboardPage } from '@/features/student/DashboardPage';
import { DocumentsPage } from '@/features/student/DocumentsPage';
import { EnrollmentPage } from '@/features/student/EnrollmentPage';
import { FinancePage } from '@/features/student/FinancePage';
import { WorkoutPage } from '@/features/student/WorkoutPage';
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
      { path: 'matricula', element: <EnrollmentPage /> },
      { path: 'treino', element: <WorkoutPage /> },
      { path: 'documentos', element: <DocumentsPage /> },
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
