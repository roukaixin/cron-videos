import { api } from "@/api";
// 媒体管理
export const mediaApi = {
    getList: () => api.get('/media/list'),
    getDetail: (id) => api.get(`/media/${id}`),
    addMedia: (data) => api.post('/media/add', data),
    updateMedia: (id, data) => api.put(`/media/update/${id}`, data),
    deleteMedia: (id) => api.delete(`/media/delete/${id}`)
};
