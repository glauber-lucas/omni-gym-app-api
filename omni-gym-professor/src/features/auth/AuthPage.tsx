import { zodResolver } from '@hookform/resolvers/zod';
import { ClipboardCheck, LockKeyhole, Mail } from 'lucide-react';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { Button } from '@/shared/components/Button';
import { Field } from '@/shared/components/Field';
import { apiError } from '@/shared/utils/format';
import { useAuth } from './AuthProvider';

const authSchema = (mode: 'login' | 'register') =>
  z
    .object({
      email: z.string().email('Informe um e-mail válido.'),
      password: z.string().min(6, 'A senha precisa ter ao menos 6 caracteres.'),
      confirmPassword: z.string().optional()
    })
    .superRefine((values, context) => {
      if (mode === 'register' && values.password !== values.confirmPassword) {
        context.addIssue({
          code: 'custom',
          message: 'As senhas precisam ser iguais.',
          path: ['confirmPassword']
        });
      }
    });

type FormValues = z.infer<ReturnType<typeof authSchema>>;

export function AuthPage({ mode }: { mode: 'login' | 'register' }) {
  const { user, login, register } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const {
    register: registerField,
    handleSubmit,
    formState: { errors }
  } = useForm<FormValues>({
    resolver: zodResolver(authSchema(mode)),
    defaultValues: {
      email: '',
      password: '',
      confirmPassword: ''
    }
  });

  if (user) {
    return <Navigate to="/" replace />;
  }

  async function onSubmit(values: FormValues) {
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      if (mode === 'register') {
        await register({ usuario: values.email, senha: values.password });
        setSuccess('Cadastro realizado com sucesso. Agora entre com seu e-mail e senha.');
        return;
      }
      await login({ identifier: values.email, password: values.password });
      navigate('/');
    } catch (caught) {
      setError(apiError(caught));
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="grid min-h-screen bg-slate-50 lg:grid-cols-[1.05fr_0.95fr]">
      <section className="relative flex min-h-[42vh] items-center overflow-hidden bg-primary-100 p-8 text-slate-950 lg:min-h-screen lg:p-14">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_18%_16%,rgba(66,165,255,0.35),transparent_34%),linear-gradient(140deg,rgba(109,221,208,0.96),rgba(209,250,244,0.96))]" />
        <div className="relative z-10 max-w-xl">
          <img src="/logo.png" alt="Omni Gym Professor" className="mb-8 h-28 w-28 object-contain drop-shadow-xl" />
          <p className="mb-3 inline-flex rounded-full bg-white/55 px-3 py-1 text-sm font-semibold backdrop-blur">
            Portal do professor
          </p>
          <h1 className="text-4xl font-black leading-tight sm:text-5xl">Operação clara para treinos seguros.</h1>
          <p className="mt-5 max-w-lg text-base text-slate-700">
            Homologue alunos, gerencie treinos adaptados, acompanhe documentos clínicos e mantenha o financeiro sob controle.
          </p>
        </div>
      </section>

      <section className="flex items-center justify-center px-4 py-10 sm:px-8">
        <form className="panel w-full max-w-md" onSubmit={handleSubmit(onSubmit)}>
          <div className="mb-8">
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary-20 text-slate-950">
              <ClipboardCheck size={24} />
            </div>
            <h2 className="text-2xl font-black">{mode === 'login' ? 'Entrar' : 'Criar instrutor'}</h2>
            <p className="muted mt-2">
              {mode === 'login' ? 'Use suas credenciais de professor.' : 'O cadastro usa a chave local definida no .env. Depois, faça login para entrar.'}
            </p>
          </div>

          <div className="space-y-4">
            <div className="relative">
              <Mail className="pointer-events-none absolute left-3 top-9 text-slate-400" size={16} />
              <Field label="E-mail" type="email" placeholder="professor@email.com" error={errors.email?.message} {...registerField('email')} />
            </div>
            <div className="relative">
              <LockKeyhole className="pointer-events-none absolute left-3 top-9 text-slate-400" size={16} />
              <Field label="Senha" type="password" placeholder="Sua senha" error={errors.password?.message} {...registerField('password')} />
            </div>
            {mode === 'register' && (
              <div className="relative">
                <LockKeyhole className="pointer-events-none absolute left-3 top-9 text-slate-400" size={16} />
                <Field
                  label="Confirmar senha"
                  type="password"
                  placeholder="Repita sua senha"
                  error={errors.confirmPassword?.message}
                  {...registerField('confirmPassword')}
                />
              </div>
            )}
          </div>

          {error && <div className="mt-5 rounded-lg bg-rose-50 p-3 text-sm font-medium text-rose-700">{error}</div>}
          {success && <div className="mt-5 rounded-lg bg-emerald-50 p-3 text-sm font-medium text-emerald-700">{success}</div>}

          <Button className="mt-6 w-full justify-center" isLoading={loading} type="submit">
            {mode === 'login' ? 'Entrar' : 'Cadastrar'}
          </Button>

          <p className="mt-6 text-center text-sm text-slate-500">
            {mode === 'login' ? 'Ainda não tem conta?' : 'Já tem cadastro?'}{' '}
            <Link className="font-bold text-slate-950 hover:underline" to={mode === 'login' ? '/cadastro' : '/login'}>
              {mode === 'login' ? 'Criar instrutor' : 'Entrar'}
            </Link>
          </p>
        </form>
      </section>
    </main>
  );
}
