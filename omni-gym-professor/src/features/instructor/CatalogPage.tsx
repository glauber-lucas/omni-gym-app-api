import { useMutation, useQueries, useQueryClient } from '@tanstack/react-query';
import { Dumbbell, Eye, ImagePlus, Plus, Save, SlidersHorizontal, X } from 'lucide-react';
import { useEffect, useRef, useState, type ChangeEvent } from 'react';
import { instructorApi } from '@/services/api/instructorApi';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/EmptyState';
import { Field, Textarea } from '@/shared/components/Field';
import { apiError } from '@/shared/utils/format';

type ExerciseFormState = {
  nome: string;
  grupoMuscular: string;
  estacaoTrabalho: string;
  estabilidadeTroncoMinima: string;
  exigenciasIds: number[];
  adaptacaoArticulacaoId: string;
  adaptacaoAcessorioId: string;
  instrucaoTexto: string;
  imagem: File | null;
  imagemPreviewUrl: string | null;
};

const emptyExercise = (): ExerciseFormState => ({
  nome: '',
  grupoMuscular: '',
  estacaoTrabalho: '',
  estabilidadeTroncoMinima: 'INDEPENDENTE',
  exigenciasIds: [],
  adaptacaoArticulacaoId: '',
  adaptacaoAcessorioId: '',
  instrucaoTexto: '',
  imagem: null,
  imagemPreviewUrl: null
});

export function CatalogPage() {
  const queryClient = useQueryClient();
  const imageInputRef = useRef<HTMLInputElement | null>(null);
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
  const [exercise, setExercise] = useState(emptyExercise);
  const [selectedImage, setSelectedImage] = useState<{ title: string; imageUrl: string } | null>(null);

  useEffect(() => {
    return () => {
      if (exercise.imagemPreviewUrl) {
        URL.revokeObjectURL(exercise.imagemPreviewUrl);
      }
    };
  }, [exercise.imagemPreviewUrl]);

  const refresh = async () => {
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'exercises'] });
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'articulations'] });
    await queryClient.invalidateQueries({ queryKey: ['instructor', 'accessories'] });
  };

  function clearImageSelection() {
    setExercise(current => ({ ...current, imagem: null, imagemPreviewUrl: null }));
    if (imageInputRef.current) {
      imageInputRef.current.value = '';
    }
  }

  function handleImageChange(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0] ?? null;

    if (!file) {
      clearImageSelection();
      return;
    }

    if (!file.type.startsWith('image/')) {
      setMessage('Selecione um arquivo de imagem válido.');
      setExercise(current => ({ ...current, imagem: null, imagemPreviewUrl: null }));
      event.target.value = '';
      return;
    }

    setMessage(null);
    setExercise(current => ({
      ...current,
      imagem: file,
      imagemPreviewUrl: URL.createObjectURL(file)
    }));
  }

  function submitExercise() {
    if (!exercise.nome.trim() || !exercise.grupoMuscular.trim() || !exercise.estacaoTrabalho.trim()) {
      setMessage('Preencha nome, grupo muscular e estação de trabalho.');
      return;
    }

    if (!exercise.exigenciasIds.length) {
      setMessage('Selecione pelo menos uma exigência articular.');
      return;
    }

    setMessage(exercise.imagem ? 'Cadastrando exercício e enviando imagem...' : 'Cadastrando exercício...');
    createExercise.mutate();
  }

  const createExercise = useMutation({
    mutationFn: () =>
      instructorApi.createExercise({
        nome: exercise.nome.trim(),
        grupoMuscular: exercise.grupoMuscular.trim(),
        estacaoTrabalho: exercise.estacaoTrabalho.trim(),
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
            : [],
        imagem: exercise.imagem
      }),
    onSuccess: async () => {
      setMessage('Exercício cadastrado.');
      setExercise(emptyExercise());
      if (imageInputRef.current) {
        imageInputRef.current.value = '';
      }
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
      <header className="glass-panel">
        <p className="muted">Catálogo global</p>
        <div className="mt-2 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="text-3xl font-black text-ink-100">Exercícios, articulações e acessórios</h2>
            <p className="muted mt-2 max-w-2xl">Organize a biblioteca que sustenta treinos adaptados e regras biomecânicas.</p>
          </div>
          <span className="badge bg-primary-10 text-primary-100">{exercises.data?.length ?? 0} exercícios</span>
        </div>
      </header>

      {message && <div className="status-banner">{message}</div>}

      <section className="grid gap-5 xl:grid-cols-[420px_1fr]">
        <form
          className="glass-panel h-fit space-y-4"
          noValidate
          onSubmit={event => {
            event.preventDefault();
            submitExercise();
          }}
        >
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-primary-20 text-primary-100">
              <Dumbbell size={20} />
            </div>
            <div>
              <h3 className="section-title">Novo exercício</h3>
              <p className="muted">Defina exigências e adaptação quando necessário.</p>
            </div>
          </div>
          <Field label="Nome" value={exercise.nome} onChange={event => setExercise({ ...exercise, nome: event.target.value })} required />
          <Field label="Grupo muscular" value={exercise.grupoMuscular} onChange={event => setExercise({ ...exercise, grupoMuscular: event.target.value })} required />
          <Field label="Estação de trabalho" value={exercise.estacaoTrabalho} onChange={event => setExercise({ ...exercise, estacaoTrabalho: event.target.value })} required />
          <div className="grid gap-1.5">
            <span className="label">Imagem do exercício</span>
            <div className="soft-card space-y-3">
              {exercise.imagemPreviewUrl && (
                <img
                  className="aspect-video w-full rounded-2xl border border-white/80 object-cover"
                  src={exercise.imagemPreviewUrl}
                  alt={exercise.nome ? `Prévia de ${exercise.nome}` : 'Prévia da imagem do exercício'}
                />
              )}
              <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <p className="text-sm font-black text-ink-100">{exercise.imagem?.name ?? 'Nenhuma imagem selecionada'}</p>
                  <p className="muted">JPG, PNG ou outro formato de imagem.</p>
                </div>
                <div className="flex items-center gap-2">
                  <label className="inline-flex min-h-11 cursor-pointer items-center gap-2 rounded-2xl border border-slate-200/80 bg-white/90 px-4 py-2.5 text-sm font-black text-ink-60 shadow-sm transition hover:-translate-y-0.5 hover:border-primary-60 hover:text-ink-100 focus-within:outline-none focus-within:ring-4 focus-within:ring-primary-20">
                    <ImagePlus size={16} />
                    Selecionar
                    <input ref={imageInputRef} className="sr-only" type="file" accept="image/*" onChange={handleImageChange} />
                  </label>
                  {exercise.imagem && (
                    <button className="icon-button" type="button" onClick={clearImageSelection} aria-label="Remover imagem selecionada">
                      <X size={16} />
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
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
          <div className="rounded-[1.35rem] border border-primary-20 bg-primary-10/60 p-4">
            <p className="mb-3 font-black text-ink-100">Adaptação opcional</p>
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
            <div className="flex items-center gap-3">
              <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-secondary-20 text-secondary-100">
                <SlidersHorizontal size={20} />
              </div>
              <h3 className="section-title">Exercícios cadastrados</h3>
            </div>
            <div className="mt-4 grid gap-3 md:grid-cols-2">
              {(exercises.data ?? []).map(item => (
                <div key={item.id} className="soft-card relative">
                  {item.imagemUrl && (
                    <button
                      className="icon-button absolute right-3 top-3"
                      type="button"
                      onClick={() => setSelectedImage({ title: item.nome, imageUrl: item.imagemUrl! })}
                      aria-label={`Ver imagem de ${item.nome}`}
                    >
                      <Eye size={16} />
                    </button>
                  )}
                  <p className="pr-12 font-black text-ink-100">{item.nome}</p>
                  <p className="muted">
                    {item.grupoMuscular} · {item.estacaoTrabalho}
                  </p>
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
              <p className="muted">{(articulations.data ?? []).map(item => item.nome).join(', ') || 'Nenhuma articulação cadastrada.'}</p>
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
              <p className="muted">{(accessories.data ?? []).map(item => item.nome).join(', ') || 'Nenhum acessório cadastrado.'}</p>
            </form>
          </div>
        </div>
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

    instructorApi
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
