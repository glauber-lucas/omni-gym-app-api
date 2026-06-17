import { useMutation, useQueries, useQueryClient } from '@tanstack/react-query';
import { Plus, Save, Trash2 } from 'lucide-react';
import { useState } from 'react';
import type { WorkoutExercisePayload } from '@/services/api/contracts';
import { instructorApi } from '@/services/api/instructorApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { Field } from '@/shared/components/Field';
import { apiError } from '@/shared/utils/format';

export function WorkoutsPage() {
  const queryClient = useQueryClient();
  const [students, exercises] = useQueries({
    queries: [
      { queryKey: ['instructor', 'students'], queryFn: instructorApi.students },
      { queryKey: ['instructor', 'exercises'], queryFn: instructorApi.exercises }
    ]
  });
  const [alunoId, setAlunoId] = useState('');
  const [nome, setNome] = useState('Ficha inicial');
  const [items, setItems] = useState<WorkoutExercisePayload[]>([]);
  const [newItem, setNewItem] = useState({
    exercicioId: '',
    series: '3',
    repeticoes: '10',
    cargaInicial: '',
    descansoSegundos: '60'
  });
  const [message, setMessage] = useState<string | null>(null);

  const mutation = useMutation({
    mutationFn: () => {
      if (!alunoId) throw new Error('Selecione um aluno.');
      return instructorApi.createWorkout(Number(alunoId), { nome, exercicios: items });
    },
    onSuccess: async () => {
      setMessage('Ficha criada e vinculada ao aluno.');
      await queryClient.invalidateQueries({ queryKey: ['instructor', 'students'] });
    },
    onError: error => setMessage(apiError(error))
  });

  function addExercise(exercicioId: number, values = newItem) {
    if (!exercicioId) {
      setMessage('Selecione um exercício para adicionar.');
      return;
    }

    setItems(current => [
      ...current,
      {
        exercicioId,
        series: Number(values.series),
        repeticoes: Number(values.repeticoes),
        cargaInicial: values.cargaInicial,
        descansoSegundos: Number(values.descansoSegundos),
        ordemExecucao: current.length + 1
      }
    ]);
    setNewItem({ exercicioId: '', series: '3', repeticoes: '10', cargaInicial: '', descansoSegundos: '60' });
  }

  function update(index: number, patch: Partial<WorkoutExercisePayload>) {
    setItems(current => current.map((item, itemIndex) => (itemIndex === index ? { ...item, ...patch } : item)));
  }

  return (
    <div className="page-shell">
      <header>
        <p className="muted">Fichas de treino</p>
        <h2 className="text-3xl font-black">Criar treino por aluno</h2>
      </header>

      {message && <div className="panel bg-primary-20 text-sm font-semibold text-slate-800">{message}</div>}

      <section className="grid gap-5 xl:grid-cols-[1fr_360px]">
        <form
          className="panel grid gap-5"
          onSubmit={event => {
            event.preventDefault();
            mutation.mutate();
          }}
        >
          <div className="grid gap-4 md:grid-cols-2">
            <label className="grid gap-1.5">
              <span className="label">Aluno</span>
              <select className="input" value={alunoId} onChange={event => setAlunoId(event.target.value)}>
                <option value="">Selecione</option>
                {(students.data ?? []).map(student => (
                  <option key={student.userId ?? student.id} value={student.userId ?? student.id}>
                    {student.name ?? student.username}
                  </option>
                ))}
              </select>
            </label>
            <Field label="Nome da ficha" value={nome} onChange={event => setNome(event.target.value)} required />
          </div>

          <div className="rounded-lg border border-slate-100 bg-slate-50 p-4">
            <div className="mb-4">
              <h3 className="font-bold text-slate-950">Adicionar exercício</h3>
              <p className="muted">Escolha um exercício cadastrado no catálogo e defina a prescrição inicial.</p>
            </div>

            <div className="grid gap-3 lg:grid-cols-[1.5fr_repeat(4,minmax(0,0.65fr))_auto]">
              <label className="grid gap-1.5">
                <span className="label">Exercício</span>
                <select className="input" value={newItem.exercicioId} onChange={event => setNewItem({ ...newItem, exercicioId: event.target.value })}>
                  <option value="">Selecione</option>
                  {(exercises.data ?? []).map(exercise => (
                    <option key={exercise.id} value={exercise.id}>
                      {exercise.nome} · {exercise.grupoMuscular}
                    </option>
                  ))}
                </select>
              </label>
              <Field label="Séries" type="number" min={1} value={newItem.series} onChange={event => setNewItem({ ...newItem, series: event.target.value })} />
              <Field label="Reps" type="number" min={1} value={newItem.repeticoes} onChange={event => setNewItem({ ...newItem, repeticoes: event.target.value })} />
              <Field label="Carga" value={newItem.cargaInicial} onChange={event => setNewItem({ ...newItem, cargaInicial: event.target.value })} />
              <Field
                label="Descanso"
                type="number"
                min={0}
                value={newItem.descansoSegundos}
                onChange={event => setNewItem({ ...newItem, descansoSegundos: event.target.value })}
              />
              <div className="flex items-end">
                <Button
                  className="w-full justify-center"
                  variant="secondary"
                  type="button"
                  onClick={() => addExercise(Number(newItem.exercicioId))}
                  disabled={!exercises.data?.length}
                >
                  <Plus size={16} />
                  Adicionar
                </Button>
              </div>
            </div>

            {!exercises.data?.length && (
              <p className="mt-3 rounded-lg bg-amber-50 px-3 py-2 text-sm font-semibold text-amber-700">
                Nenhum exercício cadastrado ainda. Cadastre exercícios na aba Catálogo antes de criar uma ficha.
              </p>
            )}
          </div>

          <div className="space-y-3">
            {items.map((item, index) => {
              const exercise = exercises.data?.find(found => found.id === item.exercicioId);
              return (
                <div key={`${item.exercicioId}-${index}`} className="rounded-lg border border-slate-100 bg-slate-50 p-4">
                  <div className="mb-4 flex items-start justify-between gap-3">
                    <div>
                      <p className="font-bold">{exercise?.nome ?? `Exercício ${item.exercicioId}`}</p>
                      <p className="muted">{exercise?.estacaoTrabalho ?? 'Estação livre'}</p>
                    </div>
                    <button className="icon-button" type="button" onClick={() => setItems(current => current.filter((_, itemIndex) => itemIndex !== index))} aria-label="Remover exercício">
                      <Trash2 size={16} />
                    </button>
                  </div>
                  <div className="grid gap-3 sm:grid-cols-5">
                    <Field label="Ordem" type="number" value={item.ordemExecucao} onChange={event => update(index, { ordemExecucao: Number(event.target.value) })} />
                    <Field label="Séries" type="number" value={item.series} onChange={event => update(index, { series: Number(event.target.value) })} />
                    <Field label="Reps" type="number" value={item.repeticoes} onChange={event => update(index, { repeticoes: Number(event.target.value) })} />
                    <Field label="Carga" value={item.cargaInicial ?? ''} onChange={event => update(index, { cargaInicial: event.target.value })} />
                    <Field label="Descanso" type="number" value={item.descansoSegundos ?? 0} onChange={event => update(index, { descansoSegundos: Number(event.target.value) })} />
                  </div>
                </div>
              );
            })}
            {!items.length && <EmptyState title="Adicione exercícios à ficha." />}
          </div>

          <Button className="w-full justify-center sm:w-fit" isLoading={mutation.isPending}>
            <Save size={16} />
            Criar ficha
          </Button>
        </form>

        <aside className="panel h-fit">
          <h3 className="section-title">Catálogo disponível</h3>
          <div className="mt-4 space-y-3">
            {(exercises.data ?? []).map(exercise => (
              <div key={exercise.id} className="rounded-lg border border-slate-100 p-3">
                <p className="font-semibold">{exercise.nome}</p>
                <p className="muted">{exercise.grupoMuscular} · {exercise.estacaoTrabalho}</p>
                <Button className="mt-3 w-full justify-center" variant="ghost" type="button" onClick={() => addExercise(exercise.id)}>
                  <Plus size={16} />
                  Adicionar
                </Button>
              </div>
            ))}
            {!exercises.data?.length && <EmptyState title="Nenhum exercício cadastrado." />}
          </div>
        </aside>
      </section>
    </div>
  );
}
