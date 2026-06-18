export type User = {
  id?: number;
  userId?: number;
  documentId?: string;
  email?: string;
  username?: string;
  name?: string;
};

export type AuthResponse = {
  jwt?: string;
  token?: string;
  refreshToken?: string;
  user?: User;
};

export type StudentProfile = {
  id?: number;
  userId?: number;
  name?: string;
  documentId?: string;
  username?: string;
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

export type OptionItem = {
  id: number;
  nome: string;
  descricao?: string;
};

export type Exercise = {
  id: number;
  nome: string;
  grupoMuscular: string;
  estacaoTrabalho: string;
  estabilidadeTroncoMinima?: string;
  exigencias?: string[];
  adaptacoes?: Array<{ id: number; articulacao: string; acessorio: string; instrucaoTexto: string }>;
  imagemUrl?: string;
};

export type WorkoutExercisePayload = {
  exercicioId: number;
  series: number;
  repeticoes: number;
  cargaInicial?: string;
  descansoSegundos?: number;
  ordemExecucao: number;
};

export type Plan = {
  id: number;
  nome: string;
  valor: number;
  duracaoMeses: number;
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

export type RevenueReport = {
  totalRecebido?: number;
  totalPendente?: number;
  totalAtrasado?: number;
  faturasPagasCount?: number;
  faturasPendentesCount?: number;
  faturasAtrasadasCount?: number;
  totalFaturasCount?: number;
};

export type MedicalDocument = {
  id: number;
  tipo: string;
  descricao?: string;
  dataUpload?: string;
  acessosCount?: number;
  downloadUrl?: string;
  ativo?: boolean;
};

export type ClinicalRecord = {
  id: number;
  alunoId: number;
  laudoMedicoUrl: string;
  observacoes?: string;
  dataAvaliacao?: string;
  dataProximaReavaliacao?: string;
};

export type AuditRecord = {
  id: number;
  username?: string;
  ipAddress?: string;
  userAgent?: string;
  dataAcesso?: string;
};
