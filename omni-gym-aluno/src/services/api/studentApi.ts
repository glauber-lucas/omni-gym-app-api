import { api, tokenStore } from './client';
import type { AuthResponse, Enrollment, Exercise, Invoice, MedicalDocument, Payment, User, Workout } from './contracts';

const tokenFrom = (data: AuthResponse) => data.jwt ?? data.token ?? '';

function persistAuthTokens(data: AuthResponse) {
  const accessToken = tokenFrom(data);

  if (!accessToken) {
    throw new Error('Resposta de login inválida.');
  }

  tokenStore.set(accessToken, data.refreshToken);
}

export const authApi = {
  async login(payload: { identifier: string; password: string }) {
    const { data } = await api.post<AuthResponse>('/auth/local', payload);
    persistAuthTokens(data);
    return data;
  },
  async register(payload: { usuario: string; senha: string }) {
    await api.post('/auth/register', { ...payload, role: 'ROLE_ALUNO' });
  },
  async me() {
    const { data } = await api.get<User>('/auth/me');
    return data;
  }
};

export const studentApi = {
  async enrollment() {
    const { data } = await api.get<Enrollment>('/aluno/matricula');
    return data;
  },
  async saveEnrollment(payload: Enrollment) {
    const { data } = await api.post<Enrollment>('/aluno/matricula', payload);
    return data;
  },
  async workout() {
    const { data } = await api.get<Workout>('/aluno/treino-diario');
    return data;
  },
  async availableExercises() {
    const { data } = await api.get<Exercise[]>('/aluno/treino/exercicios-disponiveis');
    return data;
  },
  async exerciseImage(imageUrl: string) {
    const { data } = await api.get<Blob>(imageUrl, { responseType: 'blob' });
    return data;
  },
  async editWorkout(payload: Workout) {
    const { data } = await api.put<Workout>('/aluno/treino/editar', payload);
    return data;
  },
  async invoices() {
    const { data } = await api.get<Invoice[]>('/aluno/financeiro/faturas');
    return data;
  },
  async processPayment(faturaId: number, payload: { provedor: string; metodo?: string }) {
    const { data } = await api.post<Payment>(`/aluno/financeiro/faturas/${faturaId}/processar-pagamento`, payload);
    return data;
  },
  async confirmPayment(pagamentoId: number) {
    await api.post(`/aluno/financeiro/pagamentos/${pagamentoId}/confirmar`);
  },
  async rejectPayment(pagamentoId: number) {
    await api.post(`/aluno/financeiro/pagamentos/${pagamentoId}/recusar`);
  },
  async uploadDocument(payload: { arquivo: File; tipo: string; descricao?: string; dataProximaReavaliacao?: string }) {
    const formData = new FormData();
    formData.append('arquivo', payload.arquivo);
    formData.append('tipo', payload.tipo);
    if (payload.descricao) formData.append('descricao', payload.descricao);
    if (payload.dataProximaReavaliacao) formData.append('dataProximaReavaliacao', payload.dataProximaReavaliacao);
    const { data } = await api.post<MedicalDocument>('/aluno/documentos-medicos/upload', formData);
    return data;
  },
  async deleteDocument(documentoId: number) {
    await api.delete(`/documentos/${documentoId}`);
  },
  documentDownloadUrl(documentoId: number) {
    return `${import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'}/api/documentos/${documentoId}/download`;
  }
};
