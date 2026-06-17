import { render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { AuthProvider } from './AuthProvider';
import { AuthPage } from './AuthPage';

test('renders instructor login form', () => {
  render(
    <QueryClientProvider client={new QueryClient()}>
      <MemoryRouter>
        <AuthProvider>
          <AuthPage mode="login" />
        </AuthProvider>
      </MemoryRouter>
    </QueryClientProvider>
  );

  expect(screen.getByRole('button', { name: /entrar/i })).toBeInTheDocument();
  expect(screen.getByText(/portal do professor/i)).toBeInTheDocument();
});
