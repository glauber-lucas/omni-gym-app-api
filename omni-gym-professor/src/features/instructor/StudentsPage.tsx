import { useMutation, useQueries, useQueryClient } from '@tanstack/react-query';
import { CheckCircle2, Search, ShieldCheck, Users } from 'lucide-react';
import { useMemo, useState } from 'react';
import type { StudentProfile } from '@/services/api/contracts';
import { instructorApi } from '@/services/api/instructorApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { apiError } from '@/shared/utils/format';

export function StudentsPage() {
  const queryClient = useQueryClient();
  const [students, plans, articulations] = useQueries({
    queries: [
      { queryKey: ['instructor', 'students'], queryFn: instructorApi.students },
      { queryKey: ['instructor', 'plans'], queryFn: instructorApi.plans },
      { queryKey: ['instructor', 'articulations'], queryFn: instructorApi.articulations }
    ]
  });
  const [search, setSearch] = useState('');
  const [selected, setSelected] = useState<StudentProfile | null>(null);
  const [planoId, setPlanoId] = useState('');
  const [estabilidade, setEstabilidade] = useState('PLENO');
  const [restricoesIds, setRestricoesIds] = useState<number[]>([]);
  const [bloqueioMedico, setBloqueioMedico] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const filtered = useMemo(() => {
    const term = search.toLowerCase();
    return (students.data ?? []).filter(item => `${item.name ?? ''} ${item.username ?? ''} ${item.statusMatricula ?? ''}`.toLowerCase().includes(term));
  }, [search, students.data]);

  const refresh = async () => {
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'students'] });
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'pending-students'] });
  };

  const approve = useMutation({
    mutationFn: async (student: StudentProfile) => {
      const alunoId = student.userId ?? student.id;
      if (!alunoId) throw new Error('Aluno sem identificador.');
      return planoId ? instructorApi.approveStudentWithPlan(alunoId, Number(planoId)) : instructorApi.approveStudent(alunoId);
    },
    onSuccess: async () => {
      setMessage('Matrícula homologada.');
      await refresh();
    },
    onError: error => setMessage(apiError(error))
  });

  const biomechanics = useMutation({
    mutationFn: async () => {
      const alunoId = selected?.userId ?? selected?.id;
      if (!alunoId) throw new Error('Selecione um aluno.');
      return instructorApi.saveBiomechanics(alunoId, { estabilidadeTronco: estabilidade, restricoesIds, bloqueioMedico });
    },
    onSuccess: async () => {
      setMessage('Perfil biomecânico atualizado.');
      await refresh();
    },
    onError: error => setMessage(apiError(error))
  });

  return (
    <div className="page-shell">
      <header className="glass-panel">
        <p className="muted">Matrículas & biomecânica</p>
        <div className="mt-2 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="text-3xl font-black text-ink-100">Alunos</h2>
            <p className="muted mt-2 max-w-2xl">Homologue matrículas, atribua planos e mantenha o perfil biomecânico atualizado.</p>
          </div>
          <span className="badge bg-primary-10 text-primary-100">{filtered.length} visíveis</span>
        </div>
      </header>

      {message && <div className="status-banner">{message}</div>}

      <section className="grid gap-5 xl:grid-cols-[1fr_420px]">
        <div className="panel">
          <div className="mb-5 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div className="flex items-center gap-3">
              <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-primary-20 text-primary-100">
                <Users size={20} />
              </div>
              <h3 className="section-title">Matrículas cadastradas</h3>
            </div>
            <label className="relative w-full sm:w-80">
              <Search className="pointer-events-none absolute left-4 top-3.5 text-slate-400" size={16} />
              <input className="input pl-11" placeholder="Buscar aluno" value={search} onChange={event => setSearch(event.target.value)} />
            </label>
          </div>

          <div className="hidden overflow-x-auto md:block">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Aluno</th>
                  <th>Status</th>
                  <th>Contato</th>
                  <th>Ação</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(student => (
                  <tr key={student.userId ?? student.id}>
                    <td>
                      <button className="font-black text-ink-100 hover:text-primary-100" onClick={() => setSelected(student)}>
                        {student.name ?? student.username}
                      </button>
                    </td>
                    <td><span className="badge bg-primary-10 text-primary-100">{student.statusMatricula ?? 'Sem status'}</span></td>
                    <td>{student.telefone ?? 'Não informado'}</td>
                    <td>
                      <Button variant="ghost" onClick={() => approve.mutate(student)}>
                        <CheckCircle2 size={16} />
                        Homologar
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="space-y-3 md:hidden">
            {filtered.map(student => (
              <div key={student.userId ?? student.id} className="soft-card">
                <button className="font-black text-ink-100" onClick={() => setSelected(student)}>{student.name ?? student.username}</button>
                <p className="muted">{student.statusMatricula ?? 'Sem status'} · {student.telefone ?? 'Sem telefone'}</p>
                <Button className="mt-3 w-full justify-center" variant="ghost" onClick={() => approve.mutate(student)}>
                  <CheckCircle2 size={16} />
                  Homologar
                </Button>
              </div>
            ))}
          </div>

          {!filtered.length && <EmptyState title="Nenhum aluno encontrado." />}
        </div>

        <aside className="glass-panel h-fit space-y-4">
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-secondary-20 text-secondary-100">
              <ShieldCheck size={20} />
            </div>
            <div>
              <h3 className="section-title">Ações do aluno</h3>
              <p className="muted">{selected ? selected.name ?? selected.username : 'Selecione um aluno para editar biomecânica.'}</p>
            </div>
          </div>

          <label className="grid gap-1.5">
            <span className="label">Plano para homologação</span>
            <select className="input" value={planoId} onChange={event => setPlanoId(event.target.value)}>
              <option value="">Sem plano</option>
              {(plans.data ?? []).map(plan => (
                <option key={plan.id} value={plan.id}>{plan.nome}</option>
              ))}
            </select>
          </label>

          <label className="grid gap-1.5">
            <span className="label">Estabilidade de tronco</span>
            <select className="input" value={estabilidade} onChange={event => setEstabilidade(event.target.value)}>
              <option value="PLENO">Independente</option>
              <option value="PARCIAL">Parcial</option>
              <option value="LIMITADO">Dependente</option>
            </select>
          </label>

          <div>
            <p className="label mb-2">Restrições articulares</p>
            <div className="grid gap-2">
              {(articulations.data ?? []).map(item => (
                <label key={item.id} className="flex items-center gap-2 rounded-2xl border border-slate-100 bg-white/90 px-3 py-2 text-sm font-semibold text-ink-80">
                  <input
                    type="checkbox"
                    checked={restricoesIds.includes(item.id)}
                    onChange={event =>
                      setRestricoesIds(current => (event.target.checked ? [...current, item.id] : current.filter(id => id !== item.id)))
                    }
                  />
                  {item.nome}
                </label>
              ))}
            </div>
          </div>

          <label className="flex items-center gap-2 rounded-2xl bg-rose-50 px-3 py-2 text-sm font-black text-rose-700">
            <input type="checkbox" checked={bloqueioMedico} onChange={event => setBloqueioMedico(event.target.checked)} />
            Bloqueio médico ativo
          </label>

          <Button className="w-full justify-center" disabled={!selected} isLoading={biomechanics.isPending} onClick={() => biomechanics.mutate()}>
            <ShieldCheck size={16} />
            Salvar biomecânica
          </Button>
        </aside>
      </section>
    </div>
  );
}
