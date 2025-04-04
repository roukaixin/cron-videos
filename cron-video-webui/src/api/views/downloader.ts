import {api} from "@/api";
import {Downloader} from "@/types";

// 下载器 api
export const downloaderApi = {
    getDownloaderList: () => api.get('/downloader/list'),
    addDownloader: (data: Partial<Downloader>) => api.post('/downloader/add', data),
    updateDownloader: (id: string, data: Partial<Downloader>) => api.put(`/downloader/update/${id}`, data),
    deleteDownloader: (id: string) => api.delete(`/downloader/delete/${id}`)
}