// 云盘分享管理
import {api} from "@/api";
import {CloudShare} from "@/types";

export const cloudShareApi = {
    getShares: (mediaId: string) => api.get(`/cloud-shares/${mediaId}`),
    addShare: (share: Partial<CloudShare>) => api.post('/cloud-shares/add', share),
    deleteShare: (id: number) => api.delete(`/cloud-shares/delete/${id}`),
    updateShare: (id: number,share: Partial<CloudShare>) => api.put(`/cloud-shares/${id}`, share)
}