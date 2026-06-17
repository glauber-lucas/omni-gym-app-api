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
    <main className="grid min-h-screen bg-slate-50 lg:grid-cols-[1.04fr_0.96fr]">
      <section className="relative flex min-h-[44vh] items-center overflow-hidden p-8 text-white lg:min-h-screen lg:p-14">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_18%_16%,rgba(22,135,245,0.38),transparent_32%),radial-gradient(circle_at_82%_20%,rgba(255,255,255,0.22),transparent_24%),linear-gradient(140deg,rgba(33,191,175,0.98),rgba(14,143,131,0.98)_48%,rgba(15,23,42,1))]" />
        <div className="absolute inset-x-0 bottom-0 h-44 bg-gradient-to-t from-slate-950/24 to-transparent" />
        <div className="relative z-10 max-w-xl">
          <img src="/logo.png" alt="Omni Gym Professor" className="mb-8 h-28 w-28 rounded-[1.75rem] object-cover shadow-glow" />
          <p className="mb-3 inline-flex rounded-full bg-white/16 px-4 py-2 text-sm font-black backdrop-blur">
            Portal do professor
          </p>
          <h1 className="text-4xl font-black leading-tight sm:text-5xl">Operação clara para treinos seguros.</h1>
          <p className="mt-5 max-w-lg text-base leading-7 text-white/80">
            Homologue alunos, gerencie treinos adaptados, acompanhe documentos clínicos e mantenha o financeiro sob controle.
          </p>
          <div className="mt-8 grid gap-3 sm:grid-cols-3">
            {['Alunos homologados', 'Treinos adaptados', 'Gestão financeira'].map(item => (
              <div key={item} className="rounded-2xl border border-white/18 bg-white/12 px-4 py-3 text-sm font-black backdrop-blur">
                {item}
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="flex items-center justify-center bg-[radial-gradient(circle_at_70%_20%,rgba(22,135,245,0.12),transparent_28rem),linear-gradient(180deg,#ffffff,#f2fbfa)] px-4 py-10 sm:px-8">
        <form className="glass-panel w-full max-w-md" onSubmit={handleSubmit(onSubmit)}>
          <div className="mb-8">
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-primary-20 text-primary-100 shadow-sm">
              <ClipboardCheck size={24} />
            </div>
            <h2 className="text-3xl font-black text-ink-100">{mode === 'login' ? 'Entrar' : 'Criar instrutor'}</h2>
            <p className="muted mt-2">
              {mode === 'login' ? 'Use suas credenciais de professor.' : 'O cadastro usa a chave local definida no .env. Depois, faça login para entrar.'}
            </p>
          </div>

          <div className="space-y-4">
            <div className="relative">
              <Mail className="pointer-events-none absolute left-4 top-10 text-slate-400" size={16} />
              <Field className="pl-11" label="E-mail" type="email" placeholder="professor@email.com" error={errors.email?.message} {...registerField('email')} />
            </div>
            <div className="relative">
              <LockKeyhole className="pointer-events-none absolute left-4 top-10 text-slate-400" size={16} />
              <Field className="pl-11" label="Senha" type="password" placeholder="Sua senha" error={errors.password?.message} {...registerField('password')} />
            </div>
            {mode === 'register' && (
              <div className="relative">
                <LockKeyhole className="pointer-events-none absolute left-4 top-10 text-slate-400" size={16} />
                <Field
                  className="pl-11"
                  label="Confirmar senha"
                  type="password"
                  placeholder="Repita sua senha"
                  error={errors.confirmPassword?.message}
                  {...registerField('confirmPassword')}
                />
              </div>
            )}
          </div>

          {error && <div className="mt-5 rounded-2xl bg-rose-50 p-3 text-sm font-bold text-rose-700">{error}</div>}
          {success && <div className="mt-5 rounded-2xl bg-emerald-50 p-3 text-sm font-bold text-emerald-700">{success}</div>}

          <Button className="mt-6 w-full justify-center" isLoading={loading} type="submit">
            {mode === 'login' ? 'Entrar' : 'Cadastrar'}
          </Button>

          <p className="mt-6 text-center text-sm text-ink-60">
            {mode === 'login' ? 'Ainda não tem conta?' : 'Já tem cadastro?'}{' '}
            <Link className="font-bold text-primary-100 hover:underline" to={mode === 'login' ? '/cadastro' : '/login'}>
              {mode === 'login' ? 'Criar instrutor' : 'Entrar'}
            </Link>
          </p>
        </form>
      </section>
    </main>
  );
}
