export type User = {
  id?: number;
  userId?: number;
  documentId?: string;
  email?: string;
  username?: string;
  name?: string;
  role?: string;
};

export type AuthResponse = {
  jwt?: string;
  token?: string;
  tipoToken?: string;
  expiraEmMillis?: number;
  refreshToken?: string;
  user?: User;
};

export type Enrollment = {
  id?: number;
  userId?: number;
  name?: string;
  telefone?: string;
  endereco?: string;
  contatoEmergencia?: string;
  infoFamiliar?: string;
  medicamentos?: string;
  deficiencias?: string;
  alergias?: string;
  statusMatricula?: string;
  estabilidadeTronco?: string;
  bloqueioMedico?: boolean;
  restricoes?: string[];
};

export type Exercise = {
  id?: number;
  exercicioId?: number;
  nome: string;
  grupoMuscular: string;
  estacaoTrabalho: string;
  estabilidadeTroncoMinima?: string;
  exigencias?: string[];
  exigenciasArticulares?: string[];
  adaptacoes?: Array<{ id: number; articulacao: string; acessorio: string; instrucaoTexto: string }>;
  imagemUrl?: string;
};

export type WorkoutExercise = {
  id?: number;
  exercicioId?: number;
  exercicioNome?: string;
  nomeExercicio?: string;
  grupoMuscular?: string;
  estacaoTrabalho?: string;
  series: number;
  repeticoes: number;
  cargaInicial?: string;
  descansoSegundos?: number;
  ordemExecucao: number;
  observacoes?: string[];
};

export type Workout = {
  id?: number;
  nome: string;
  ativa?: boolean;
  dataCriacao?: string;
  exercicios: WorkoutExercise[];
};

export type MedicalDocument = {
  id: number;
  tipo: string;
  descricao?: string;
  mimeType?: string;
  tamanhoBytes?: number;
  dataUpload?: string;
  dataProximaReavaliacao?: string;
  criadoPor?: string;
  acessosCount?: number;
  ativo?: boolean;
  downloadUrl?: string;
};

export type Invoice = {
  id: number;
  alunoId?: number;
  alunoNome?: string;
  planoId?: number;
  planoNome?: string;
  valorOriginal?: number;
  desconto?: number;
  valorCobrado?: number;
  valorPago?: number;
  dataVencimento?: string;
  dataPagamento?: string;
  status?: string;
};

export type Payment = {
  id: number;
  faturaId: number;
  provedor: string;
  status: string;
  urlPagamento?: string;
  idTransacaoExterna?: string;
  dataCriacao?: string;
};
