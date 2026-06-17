import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
const ACCESS_KEY = 'omniGymProfessor.accessToken';
const REFRESH_KEY = 'omniGymProfessor.refreshToken';

export const tokenStore = {
  getAccess: () => localStorage.getItem(ACCESS_KEY),
  getRefresh: () => localStorage.getItem(REFRESH_KEY),
  set(accessToken: string, refreshToken?: string) {
    localStorage.setItem(ACCESS_KEY, accessToken);
    if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken);
  },
  clear() {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
  }
};

export const api = axios.create({
  baseURL: API_BASE_URL
});

api.interceptors.request.use(config => {
  const token = tokenStore.getAccess();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  response => response,
  async error => {
    const original = error.config;
    const refreshToken = tokenStore.getRefresh();

    if (error.response?.status === 401 && refreshToken && !original?._retry) {
      original._retry = true;
      const response = await axios.post(`${API_BASE_URL}/auth/refresh-token`, { refreshToken });
      const access = response.data.jwt ?? response.data.token;
      tokenStore.set(access, response.data.refreshToken);
      original.headers.Authorization = `Bearer ${access}`;
      return api(original);
    }

    if (error.response?.status === 401) {
      tokenStore.clear();
    }

    return Promise.reject(error);
  }
);

export const apiBaseUrl = API_BASE_URL;
