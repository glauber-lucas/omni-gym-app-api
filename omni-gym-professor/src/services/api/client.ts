import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
const ACCESS_KEY = 'omniGymProfessor.accessToken';
const REFRESH_KEY = 'omniGymProfessor.refreshToken';
export const AUTH_LOGOUT_EVENT = 'omniGymProfessor.authLogout';

let refreshPromise: Promise<string> | null = null;

function isAuthRequest(url?: string) {
  return Boolean(url && ['/auth/local', '/auth/login', '/auth/register', '/auth/refresh-token'].some(path => url.includes(path)));
}

function notifyLogout() {
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new Event(AUTH_LOGOUT_EVENT));
  }
}

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
    notifyLogout();
  }
};

function refreshAccessToken(refreshToken: string) {
  if (!refreshPromise) {
    refreshPromise = axios
      .post(`${API_BASE_URL}/auth/refresh-token`, { refreshToken })
      .then(response => {
        const access = response.data.jwt ?? response.data.token;

        if (!access) {
          throw new Error('Resposta de renovação de sessão inválida.');
        }

        tokenStore.set(access, response.data.refreshToken);
        return access;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
}

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
    const isAuth = isAuthRequest(original?.url);

    if (error.response?.status === 401 && refreshToken && original && !original._retry && !isAuth) {
      original._retry = true;

      try {
        const access = await refreshAccessToken(refreshToken);
        original.headers = original.headers ?? {};
        original.headers.Authorization = `Bearer ${access}`;
        return api(original);
      } catch (refreshError) {
        tokenStore.clear();
        return Promise.reject(refreshError);
      }
    }

    if (error.response?.status === 401 && !isAuth) {
      tokenStore.clear();
    }

    return Promise.reject(error);
  }
);

export const apiBaseUrl = API_BASE_URL;
