import axios from 'axios';

export const BACKEND_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';

const api = axios.create({ baseURL: BACKEND_URL });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('tnsmartbus_admin_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('tnsmartbus_admin_token');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export default api;
