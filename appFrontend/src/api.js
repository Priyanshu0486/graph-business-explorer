import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const fetchGraphOverview = async () => {
  const response = await api.get('/graph/overview');
  return response.data;
};

export const fetchEntities = async (type, page = 0, size = 20) => {
  const response = await api.get(`/entities/${type}`, { params: { page, size } });
  return response.data;
};

export const fetchEntityDetail = async (type, id) => {
  const response = await api.get(`/entities/${type}/${id}`);
  return response.data;
};

export const sendChatQuery = async (question) => {
  const response = await api.post('/chat/query', { question });
  return response.data;
};

export default api;
