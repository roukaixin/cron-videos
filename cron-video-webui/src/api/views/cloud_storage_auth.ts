import {CloudStorageAuth} from "@/types";
import {api} from "@/api";

// 网盘认证信息管理 API

export const cloudStorageAuthApi = {
    getAuths: () => api.get('/cloud-storage-auth/list'),
    addAuth: (data: CloudStorageAuth) => api.post('/cloud-storage-auth/add', data),
    updateAuth: (id: number, data: CloudStorageAuth) => api.put(`/cloud-storage-auth/update/${id}`, data),
    deleteAuth: (id: number) => api.delete(`/cloud-storage-auth/delete/${id}`)
}