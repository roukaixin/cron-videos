import { api } from "@/api";
// 网盘认证信息管理 API
export const cloudStorageAuthApi = {
    getAuths: () => api.get('/cloud-storage-auth/list'),
    addAuth: (data) => api.post('/cloud-storage-auth/add', data),
    updateAuth: (id, data) => api.put(`/cloud-storage-auth/update/${id}`, data),
    deleteAuth: (id) => api.delete(`/cloud-storage-auth/delete/${id}`)
};
