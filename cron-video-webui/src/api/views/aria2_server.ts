import {api} from "@/api";

// Aria2 连接管理
export const aria2ServerApi = {
    getConnections: () => api.get('/aria2/list'),
    addConnection: (data: { ip: string; port: number; secret: string | null; weight: number }) =>
        api.post('/aria2/add', data),
    updateConnection: (id: number, data: { ip: string; port: number; secret: string | null; weight: number }) =>
        api.put(`/aria2/update/${id}`, data),
    deleteConnection: (id: number) => api.delete(`/aria2/delete/${id}`)
}