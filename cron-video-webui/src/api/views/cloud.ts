// 云盘分享管理
import {api} from "@/api";
import {CloudShare} from "@/types";

export const cloudApi = {
    getShares: (mediaId: string) => api.get(`/cloud/${mediaId}`),
    addShare: (share: Partial<CloudShare>) => api.post('/cloud/add', share),
    deleteShare: (id: number) => api.delete(`/cloud/delete/${id}`),
    updateShare: (id: number,share: Partial<CloudShare>) => api.put(`/cloud/${id}`, share)
}