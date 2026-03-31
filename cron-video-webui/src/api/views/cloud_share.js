// 云盘分享管理
import { api } from "@/api";
export const cloudShareApi = {
    getShares: (mediaId) => api.get(`/cloud-shares/${mediaId}`),
    addShare: (share) => api.post('/cloud-shares/add', share),
    deleteShare: (id) => api.delete(`/cloud-shares/delete/${id}`),
    updateShare: (id, share) => api.put(`/cloud-shares/${id}`, share)
};
