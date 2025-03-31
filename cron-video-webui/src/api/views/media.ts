import {api} from "@/api";
import {Media} from "@/types";

// 媒体管理
export const mediaApi = {
    getList: () => api.get('/media/list'),
    getDetail: (id: number) => api.get(`/media/${id}`),
    addMedia: (data: Partial<Media>) => api.post('/media/add', data),
    updateMedia: (id: string, data: Partial<Media>) =>
        api.put(`/media/update/${id}`, data),
    deleteMedia: (id: number) => api.delete(`/media/delete/${id}`)
}