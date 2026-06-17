import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Plus, Save, Trash2 } from 'lucide-react';
import { useEffect, useState } from 'react';
import type { WorkoutExercise } from '@/services/api/contracts';
import { studentApi } from '@/services/api/studentApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { Field } from '@/shared/components/Field';
import { apiError } from '@/shared/utils/format';

export function WorkoutPage() {
  const queryClient = useQueryClient();
  const workout = useQuery({ queryKey: ['student', 'workout'], queryFn: studentApi.workout, retry: false });
  const exercises = useQuery({ queryKey: ['student', 'available-exercises'], queryFn: studentApi.availableExercises, retry: false });
  const [name, setName] = useState('Ficha ativa');
  const [items, setItems] = useState<WorkoutExercise[]>([]);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    if (workout.data) {
      setName(workout.data.nome ?? 'Ficha ativa');
      setItems(workout.data.exercicios ?? []);
    }
  }, [workout.data]);

  const mutation = useMutation({
    mutationFn: studentApi.editWorkout,
    onSuccess: async () => {
      setMessage('Treino atualizado com validação de acessibilidade.');
      await queryClient.invalidateQueries({ queryKey: ['student', 'workout'] });
    },
    onError: error => setMessage(apiError(error))
  });

  function addExercise(exercicioId: number) {
    const exercise = exercises.data?.find(item => item.id === exercicioId);
    setItems(current => [
      ...current,
      {
        exercicioId,
        exercicioNome: exercise?.nome,
        estacaoTrabalho: exercise?.estacaoTrabalho,
        series: 3,
        repeticoes: 10,
        cargaInicial: '',
        descansoSegundos: 60,
        ordemExecucao: current.length + 1
      }
    ]);
  }

  function update(index: number, patch: Partial<WorkoutExercise>) {
    setItems(current => current.map((item, itemIndex) => (itemIndex === index ? { ...item, ...patch } : item)));
  }

  return (
    <div className="page-shell">
      <header>
        <p className="muted">Treino diário</p>
        <h2 className="text-3xl font-black">Ficha adaptada</h2>
      </header>

      <section className="grid gap-5 xl:grid-cols-[1fr_360px]">
        <form
          className="panel grid gap-5"
          onSubmit={event => {
            event.preventDefault();
            mutation.mutate({ nome: name, exercicios: items });
          }}
        >
          <Field label="Nome da ficha" value={name} onChange={event => setName(event.target.value)} />
          <div className="space-y-3">
            {items.map((item, index) => (
              <div key={`${item.exercicioId}-${index}`} className="rounded-lg border border-slate-100 bg-slate-50 p-4">
                <div className="mb-4 flex items-start justify-between gap-3">
                  <div>
                    <p className="font-bold">{item.exercicioNome ?? `Exercício ${item.exercicioId}`}</p>
                    <p className="muted">{item.estacaoTrabalho ?? 'Estação livre'}</p>
                  </div>
                  <button
                    type="button"
                    className="icon-button"
                    onClick={() => setItems(current => current.filter((_, itemIndex) => itemIndex !== index))}
                    aria-label="Remover exercício"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
                <div className="grid gap-3 sm:grid-cols-5">
                  <Field label="Ordem" type="number" value={item.ordemExecucao} onChange={event => update(index, { ordemExecucao: Number(event.target.value) })} />
                  <Field label="Séries" type="number" value={item.series} onChange={event => update(index, { series: Number(event.target.value) })} />
                  <Field label="Reps" type="number" value={item.repeticoes} onChange={event => update(index, { repeticoes: Number(event.target.value) })} />
                  <Field label="Carga" value={item.cargaInicial ?? ''} onChange={event => update(index, { cargaInicial: event.target.value })} />
                  <Field
                    label="Descanso"
                    type="number"
                    value={item.descansoSegundos ?? 0}
                    onChange={event => update(index, { descansoSegundos: Number(event.target.value) })}
                  />
                </div>
              </div>
            ))}
            {!items.length && <EmptyState title="Nenhum exercício na ficha." />}
          </div>

          {message && <div className="rounded-lg bg-slate-50 p-3 text-sm font-semibold text-slate-700">{message}</div>}

          <Button className="w-full justify-center sm:w-fit" isLoading={mutation.isPending}>
            <Save size={16} />
            Salvar edição
          </Button>
        </form>

        <aside className="panel h-fit">
          <h3 className="section-title">Exercícios disponíveis</h3>
          <p className="muted mt-1">A lista já exclui exercícios bloqueados biomecanicamente.</p>
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
            {!exercises.data?.length && <EmptyState title="Nenhum exercício disponível." />}
          </div>
        </aside>
      </section>
    </div>
  );
}
