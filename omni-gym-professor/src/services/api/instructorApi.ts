import { api, apiBaseUrl, tokenStore } from './client';
import type {
  AuditRecord,
  AuthResponse,
  ClinicalRecord,
  Exercise,
  Invoice,
  MedicalDocument,
  OptionItem,
  Plan,
  RevenueReport,
  StudentProfile,
  User,
  WorkoutExercisePayload
} from './contracts';

const tokenFrom = (data: AuthResponse) => data.jwt ?? data.token ?? '';

export const authApi = {
  async login(payload: { identifier: string; password: string }) {
    const { data } = await api.post<AuthResponse>('/auth/local', payload);
    tokenStore.set(tokenFrom(data), data.refreshToken);
    return data;
  },
  async register(payload: { usuario: string; senha: string }) {
    await api.post('/auth/register', {
      ...payload,
      role: 'INSTRUTOR',
      instructorSecret: import.meta.env.VITE_INSTRUCTOR_SECRET ?? 'secret-instructor-key'
    });
  },
  async me() {
    const { data } = await api.get<User>('/auth/me');
    return data;
  }
};

export const instructorApi = {
  async students() {
    const { data } = await api.get<StudentProfile[]>('/instrutor/matriculas');
    return data;
  },
  async pendingStudents() {
    const { data } = await api.get<StudentProfile[]>('/instrutor/matriculas/pendentes');
    return data;
  },
  async student(alunoId: number) {
    const { data } = await api.get<StudentProfile>(`/instrutor/matriculas/${alunoId}`);
    return data;
  },
  async approveStudent(alunoId: number) {
    const { data } = await api.post<StudentProfile>(`/instrutor/matriculas/${alunoId}/homologar`);
    return data;
  },
  async approveStudentWithPlan(alunoId: number, planoId: number) {
    const { data } = await api.post<StudentProfile>(`/instrutor/matriculas/${alunoId}/homologar-com-plano`, { planoId });
    return data;
  },
  async saveBiomechanics(alunoId: number, payload: { estabilidadeTronco: string; restricoesIds: number[]; bloqueioMedico: boolean }) {
    const { data } = await api.post<StudentProfile>(`/instrutor/alunos/${alunoId}/perfil-biomecanico`, payload);
    return data;
  },
  async biomechanicsHistory(alunoId: number) {
    const { data } = await api.get(`/instrutor/alunos/${alunoId}/perfil-biomecanico/historico`);
    return data as Array<Record<string, unknown>>;
  },
  async exercises() {
    const { data } = await api.get<Exercise[]>('/exercicios');
    return data;
  },
  async createExercise(payload: {
    nome: string;
    grupoMuscular: string;
    estacaoTrabalho: string;
    estabilidadeTroncoMinima: string;
    exigenciasIds: number[];
    adaptacoes: Array<{ articulacaoId: number; acessorioId: number; instrucaoTexto: string }>;
  }) {
    const { data } = await api.post<Exercise>('/exercicios', payload);
    return data;
  },
  async articulations() {
    const { data } = await api.get<OptionItem[]>('/articulacoes');
    return data;
  },
  async createArticulation(nome: string) {
    const { data } = await api.post<OptionItem>('/articulacoes', { nome });
    return data;
  },
  async accessories() {
    const { data } = await api.get<OptionItem[]>('/acessorios');
    return data;
  },
  async createAccessory(nome: string) {
    const { data } = await api.post<OptionItem>('/acessorios', { nome });
    return data;
  },
  async createWorkout(alunoId: number, payload: { nome: string; exercicios: WorkoutExercisePayload[] }) {
    const { data } = await api.post(`/instrutor/alunos/${alunoId}/treinos`, payload);
    return data;
  },
  async addObservation(treinoExercicioId: number, texto: string) {
    const { data } = await api.post(`/instrutor/treinos/${treinoExercicioId}/observacoes`, { texto });
    return data;
  },
  async documents(alunoId: number, tipo?: string) {
    const url = tipo
      ? `/instrutor/alunos/${alunoId}/documentos-medicos/tipo?tipo=${encodeURIComponent(tipo)}`
      : `/instrutor/alunos/${alunoId}/documentos-medicos`;
    const { data } = await api.get<MedicalDocument[]>(url);
    return data;
  },
  async clinicalRecords(alunoId: number) {
    const { data } = await api.get<ClinicalRecord[]>(`/instrutor/alunos/${alunoId}/dossie-clinico`);
    return data;
  },
  async createClinicalRecord(alunoId: number, payload: { laudoMedicoUrl: string; observacoes?: string; dataAvaliacao: string; dataProximaReavaliacao: string }) {
    const { data } = await api.post<ClinicalRecord>(`/instrutor/alunos/${alunoId}/dossie-clinico`, payload);
    return data;
  },
  async audit(documentoId: number) {
    const { data } = await api.get<AuditRecord[]>(`/instrutor/documentos/${documentoId}/historico-acesso`);
    return data;
  },
  async deleteDocument(documentoId: number) {
    await api.delete(`/documentos/${documentoId}`);
  },
  documentDownloadUrl(documentoId: number) {
    return `${apiBaseUrl}/api/documentos/${documentoId}/download`;
  },
  async plans() {
    const { data } = await api.get<Plan[]>('/instrutor/financeiro/planos');
    return data;
  },
  async createPlan(payload: { nome: string; valor: number; duracaoMeses: number }) {
    const { data } = await api.post<Plan>('/instrutor/financeiro/planos', payload);
    return data;
  },
  async invoices(status?: string) {
    const { data } = await api.get<Invoice[]>('/instrutor/financeiro/faturas', { params: status ? { status } : undefined });
    return data;
  },
  async createInvoice(alunoId: number, payload: { planoId?: number; valor?: number; dataVencimento: string }) {
    const { data } = await api.post<Invoice>(`/instrutor/financeiro/alunos/${alunoId}/faturas`, payload);
    return data;
  },
  async payInvoice(faturaId: number, valorPago?: number) {
    const { data } = await api.post<Invoice>(`/instrutor/financeiro/faturas/${faturaId}/pagar`, valorPago ? { valorPago } : undefined);
    return data;
  },
  async discountInvoice(faturaId: number, desconto: number) {
    const { data } = await api.post<Invoice>(`/instrutor/financeiro/faturas/${faturaId}/desconto`, { desconto });
    return data;
  },
  async createSubscription(alunoId: number, planoId: number) {
    const { data } = await api.post(`/instrutor/financeiro/alunos/${alunoId}/assinatura`, { planoId });
    return data;
  },
  async subscriptions(alunoId: number) {
    const { data } = await api.get(`/instrutor/financeiro/alunos/${alunoId}/assinaturas`);
    return data as Array<Record<string, unknown>>;
  },
  async cancelSubscription(assinaturaId: number) {
    await api.delete(`/instrutor/financeiro/assinatura/${assinaturaId}/cancelar`);
  },
  async revenueReport() {
    const { data } = await api.get<RevenueReport>('/instrutor/financeiro/relatorio-faturamento');
    return data;
  }
};
