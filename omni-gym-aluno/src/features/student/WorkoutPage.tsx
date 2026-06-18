import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Activity, Eye, Plus, Save, Trash2, X } from 'lucide-react';
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
  const [selectedImage, setSelectedImage] = useState<{ title: string; imageUrl: string } | null>(null);

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
    const exercise = exercises.data?.find(item => (item.id ?? item.exercicioId) === exercicioId);
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
      <header className="glass-panel">
        <p className="muted">Treino diário</p>
        <div className="mt-2 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="text-3xl font-black text-ink-100">Ficha adaptada</h2>
            <p className="muted mt-2 max-w-2xl">Edite a sequência do treino respeitando as restrições biomecânicas disponíveis no catálogo.</p>
          </div>
          <span className="badge bg-primary-10 text-primary-100">{items.length} exercícios</span>
        </div>
      </header>

      <section className="grid gap-5 xl:grid-cols-[1fr_360px]">
        <form
          className="panel grid gap-5"
          onSubmit={event => {
            event.preventDefault();
            mutation.mutate({ nome: name, exercicios: items });
          }}
        >
          <div className="rounded-[1.35rem] border border-primary-20 bg-primary-10/70 p-4">
            <Field label="Nome da ficha" value={name} onChange={event => setName(event.target.value)} />
          </div>
          <div className="space-y-3">
            {items.map((item, index) => (
              <div key={`${item.exercicioId}-${index}`} className="rounded-[1.35rem] border border-slate-100 bg-white/90 p-4 shadow-sm">
                <div className="mb-4 flex items-start justify-between gap-3">
                  <div className="flex items-start gap-3">
                    <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br from-primary-100 to-secondary-100 text-sm font-black text-white">
                      {item.ordemExecucao}
                    </span>
                    <div>
                      <p className="font-black text-ink-100">{item.exercicioNome ?? `Exercício ${item.exercicioId}`}</p>
                      <p className="muted">{item.estacaoTrabalho ?? 'Estação livre'}</p>
                    </div>
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

          {message && <div className="status-banner">{message}</div>}

          <Button className="w-full justify-center sm:w-fit" isLoading={mutation.isPending}>
            <Save size={16} />
            Salvar edição
          </Button>
        </form>

        <aside className="glass-panel h-fit">
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-primary-10 text-primary-100">
              <Activity size={20} />
            </div>
            <div>
              <h3 className="section-title">Exercícios disponíveis</h3>
              <p className="muted">A lista já exclui exercícios bloqueados biomecanicamente.</p>
            </div>
          </div>
          <div className="mt-4 space-y-3">
            {(exercises.data ?? []).map(exercise => {
              const exerciseId = exercise.id ?? exercise.exercicioId;

              return (
                <div key={exerciseId ?? exercise.nome} className="relative rounded-2xl border border-slate-100 bg-white/90 p-3 shadow-sm">
                  {exercise.imagemUrl && (
                    <button
                      className="icon-button absolute right-3 top-3"
                      type="button"
                      onClick={() => setSelectedImage({ title: exercise.nome, imageUrl: exercise.imagemUrl! })}
                      aria-label={`Ver imagem de ${exercise.nome}`}
                    >
                      <Eye size={16} />
                    </button>
                  )}
                  <p className="pr-12 font-black text-ink-100">{exercise.nome}</p>
                  <p className="muted">
                    {exercise.grupoMuscular} · {exercise.estacaoTrabalho}
                  </p>
                  <Button
                    className="mt-3 w-full justify-center"
                    variant="ghost"
                    type="button"
                    onClick={() => exerciseId && addExercise(exerciseId)}
                    disabled={!exerciseId}
                  >
                    <Plus size={16} />
                    Adicionar
                  </Button>
                </div>
              );
            })}
            {!exercises.data?.length && <EmptyState title="Nenhum exercício disponível." />}
          </div>
        </aside>
      </section>
      <ExerciseImageDialog image={selectedImage} onClose={() => setSelectedImage(null)} />
    </div>
  );
}

function ExerciseImageDialog({ image, onClose }: { image: { title: string; imageUrl: string } | null; onClose: () => void }) {
  const [objectUrl, setObjectUrl] = useState<string | null>(null);
  const [hasError, setHasError] = useState(false);

  useEffect(() => {
    if (!image) return;

    let cancelled = false;
    let nextObjectUrl: string | null = null;

    setObjectUrl(null);
    setHasError(false);

    studentApi
      .exerciseImage(image.imageUrl)
      .then(blob => {
        if (cancelled) return;
        nextObjectUrl = URL.createObjectURL(blob);
        setObjectUrl(nextObjectUrl);
      })
      .catch(() => {
        if (!cancelled) setHasError(true);
      });

    return () => {
      cancelled = true;
      if (nextObjectUrl) {
        URL.revokeObjectURL(nextObjectUrl);
      }
    };
  }, [image]);

  if (!image) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/70 p-4 backdrop-blur-sm" role="dialog" aria-modal="true">
      <div className="w-full max-w-4xl rounded-[1.75rem] border border-white/70 bg-white p-4 shadow-soft">
        <div className="mb-3 flex items-center justify-between gap-3">
          <h3 className="section-title">{image.title}</h3>
          <button className="icon-button" type="button" onClick={onClose} aria-label="Fechar imagem">
            <X size={16} />
          </button>
        </div>
        <div className="flex min-h-64 items-center justify-center overflow-hidden rounded-2xl bg-slate-100">
          {objectUrl && <img className="max-h-[70vh] w-full object-contain" src={objectUrl} alt={`Imagem de ${image.title}`} />}
          {!objectUrl && !hasError && <p className="muted p-6">Carregando imagem...</p>}
          {hasError && <p className="muted p-6">Não foi possível carregar a imagem.</p>}
        </div>
      </div>
    </div>
  );
}
