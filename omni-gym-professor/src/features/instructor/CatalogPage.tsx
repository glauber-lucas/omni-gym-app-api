import { useMutation, useQueries, useQueryClient } from '@tanstack/react-query';
import { Plus, Save } from 'lucide-react';
import { useState } from 'react';
import { instructorApi } from '@/services/api/instructorApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { Field, Textarea } from '@/shared/components/Field';
import { apiError } from '@/shared/utils/format';

export function CatalogPage() {
  const queryClient = useQueryClient();
  const [exercises, articulations, accessories] = useQueries({
    queries: [
      { queryKey: ['instructor', 'exercises'], queryFn: instructorApi.exercises },
      { queryKey: ['instructor', 'articulations'], queryFn: instructorApi.articulations },
      { queryKey: ['instructor', 'accessories'], queryFn: instructorApi.accessories }
    ]
  });
  const [message, setMessage] = useState<string | null>(null);
  const [newArticulation, setNewArticulation] = useState('');
  const [newAccessory, setNewAccessory] = useState('');
  const [exercise, setExercise] = useState({
    nome: '',
    grupoMuscular: '',
    estacaoTrabalho: '',
    estabilidadeTroncoMinima: 'INDEPENDENTE',
    exigenciasIds: [] as number[],
    adaptacaoArticulacaoId: '',
    adaptacaoAcessorioId: '',
    instrucaoTexto: ''
  });

  const refresh = async () => {
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'exercises'] });
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'articulations'] });
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'accessories'] });
  };

  const createExercise = useMutation({
    mutationFn: () =>
      instructorApi.createExercise({
        nome: exercise.nome,
        grupoMuscular: exercise.grupoMuscular,
        estacaoTrabalho: exercise.estacaoTrabalho,
        estabilidadeTroncoMinima: exercise.estabilidadeTroncoMinima,
        exigenciasIds: exercise.exigenciasIds,
        adaptacoes:
          exercise.adaptacaoArticulacaoId && exercise.adaptacaoAcessorioId && exercise.instrucaoTexto
            ? [
                {
                  articulacaoId: Number(exercise.adaptacaoArticulacaoId),
                  acessorioId: Number(exercise.adaptacaoAcessorioId),
                  instrucaoTexto: exercise.instrucaoTexto
                }
              ]
            : []
      }),
    onSuccess: async () => {
      setMessage('Exercício cadastrado.');
      setExercise({
        nome: '',
        grupoMuscular: '',
        estacaoTrabalho: '',
        estabilidadeTroncoMinima: 'INDEPENDENTE',
        exigenciasIds: [],
        adaptacaoArticulacaoId: '',
        adaptacaoAcessorioId: '',
        instrucaoTexto: ''
      });
      await refresh();
    },
    onError: error => setMessage(apiError(error))
  });

  const createOption = useMutation({
    mutationFn: async ({ type, nome }: { type: 'articulation' | 'accessory'; nome: string }) =>
      type === 'articulation' ? instructorApi.createArticulation(nome) : instructorApi.createAccessory(nome),
    onSuccess: async () => {
      setNewArticulation('');
      setNewAccessory('');
      setMessage('Item cadastrado.');
      await refresh();
    },
    onError: error => setMessage(apiError(error))
  });

  return (
    <div className="page-shell">
      <header>
        <p className="muted">Catálogo global</p>
        <h2 className="text-3xl font-black">Exercícios, articulações e acessórios</h2>
      </header>

      {message && <div className="panel bg-primary-20 text-sm font-semibold text-slate-800">{message}</div>}

      <section className="grid gap-5 xl:grid-cols-[420px_1fr]">
        <form
          className="panel h-fit space-y-4"
          onSubmit={event => {
            event.preventDefault();
            createExercise.mutate();
          }}
        >
          <h3 className="section-title">Novo exercício</h3>
          <Field label="Nome" value={exercise.nome} onChange={event => setExercise({ ...exercise, nome: event.target.value })} required />
          <Field label="Grupo muscular" value={exercise.grupoMuscular} onChange={event => setExercise({ ...exercise, grupoMuscular: event.target.value })} required />
          <Field label="Estação de trabalho" value={exercise.estacaoTrabalho} onChange={event => setExercise({ ...exercise, estacaoTrabalho: event.target.value })} required />
          <label className="grid gap-1.5">
            <span className="label">Estabilidade mínima</span>
            <select className="input" value={exercise.estabilidadeTroncoMinima} onChange={event => setExercise({ ...exercise, estabilidadeTroncoMinima: event.target.value })}>
              <option value="INDEPENDENTE">Independente</option>
              <option value="PARCIAL">Parcial</option>
              <option value="DEPENDENTE">Dependente</option>
            </select>
          </label>
          <div>
            <p className="label mb-2">Exigências articulares</p>
            <div className="grid gap-2">
              {(articulations.data ?? []).map(item => (
                <label key={item.id} className="flex items-center gap-2 rounded-lg bg-slate-50 px-3 py-2 text-sm">
                  <input
                    type="checkbox"
                    checked={exercise.exigenciasIds.includes(item.id)}
                    onChange={event =>
                      setExercise(current => ({
                        ...current,
                        exigenciasIds: event.target.checked
                          ? [...current.exigenciasIds, item.id]
                          : current.exigenciasIds.filter(id => id !== item.id)
                      }))
                    }
                  />
                  {item.nome}
                </label>
              ))}
            </div>
          </div>
          <div className="rounded-lg bg-slate-50 p-3">
            <p className="mb-3 font-semibold">Adaptação opcional</p>
            <div className="grid gap-3">
              <select className="input" value={exercise.adaptacaoArticulacaoId} onChange={event => setExercise({ ...exercise, adaptacaoArticulacaoId: event.target.value })}>
                <option value="">Articulação em conflito</option>
                {(articulations.data ?? []).map(item => <option key={item.id} value={item.id}>{item.nome}</option>)}
              </select>
              <select className="input" value={exercise.adaptacaoAcessorioId} onChange={event => setExercise({ ...exercise, adaptacaoAcessorioId: event.target.value })}>
                <option value="">Acessório</option>
                {(accessories.data ?? []).map(item => <option key={item.id} value={item.id}>{item.nome}</option>)}
              </select>
              <Textarea label="Instrução" value={exercise.instrucaoTexto} onChange={event => setExercise({ ...exercise, instrucaoTexto: event.target.value })} />
            </div>
          </div>
          <Button className="w-full justify-center" isLoading={createExercise.isPending}>
            <Save size={16} />
            Cadastrar exercício
          </Button>
        </form>

        <div className="grid gap-5">
          <div className="panel">
            <h3 className="section-title">Exercícios cadastrados</h3>
            <div className="mt-4 grid gap-3 md:grid-cols-2">
              {(exercises.data ?? []).map(item => (
                <div key={item.id} className="rounded-lg bg-slate-50 p-4">
                  <p className="font-bold">{item.nome}</p>
                  <p className="muted">{item.grupoMuscular} · {item.estacaoTrabalho}</p>
                  <p className="muted mt-2">Exigências: {item.exigencias?.join(', ') || 'Não informadas'}</p>
                </div>
              ))}
            </div>
            {!exercises.data?.length && <EmptyState title="Nenhum exercício cadastrado." />}
          </div>

          <div className="grid gap-5 lg:grid-cols-2">
            <form
              className="panel space-y-3"
              onSubmit={event => {
                event.preventDefault();
                createOption.mutate({ type: 'articulation', nome: newArticulation });
              }}
            >
              <h3 className="section-title">Articulações</h3>
              <Field label="Nova articulação" value={newArticulation} onChange={event => setNewArticulation(event.target.value)} />
              <Button variant="ghost"><Plus size={16} />Adicionar</Button>
              <p className="muted">{(articulations.data ?? []).map(item => item.nome).join(', ')}</p>
            </form>

            <form
              className="panel space-y-3"
              onSubmit={event => {
                event.preventDefault();
                createOption.mutate({ type: 'accessory', nome: newAccessory });
              }}
            >
              <h3 className="section-title">Acessórios</h3>
              <Field label="Novo acessório" value={newAccessory} onChange={event => setNewAccessory(event.target.value)} />
              <Button variant="ghost"><Plus size={16} />Adicionar</Button>
              <p className="muted">{(accessories.data ?? []).map(item => item.nome).join(', ')}</p>
            </form>
          </div>
        </div>
      </section>
    </div>
  );
}
