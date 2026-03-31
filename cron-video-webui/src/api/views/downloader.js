import { api } from "@/api";
// 下载器 api
export const downloaderApi = {
    getDownloaderList: () => api.get('/downloader/list'),
    addDownloader: (data) => api.post('/downloader/add', data),
    updateDownloader: (id, data) => api.put(`/downloader/update/${id}`, data),
    deleteDownloader: (id) => api.delete(`/downloader/delete/${id}`)
};
