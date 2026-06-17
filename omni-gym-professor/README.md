# Omni Gym Professor

Frontend React + Vite para o portal web administrativo do professor/instrutor Omni Gym.

## Stack

- React + TypeScript + Vite
- React Router
- TanStack Query
- Axios com JWT e refresh token
- React Hook Form + Zod
- Tailwind CSS
- Vitest + Testing Library

## Configuração

Copie `.env.example` para `.env` se precisar alterar a URL da API ou a chave local de cadastro de instrutor.

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_INSTRUCTOR_SECRET=secret-instructor-key
```

## Scripts

```bash
npm install
npm run dev
npm run typecheck
npm run lint
npm run test
npm run build
```

O app roda por padrão em `http://localhost:5174`.
