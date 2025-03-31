import {api, PageResponse, Response} from "@/api";


export const aria2DownloadTaskApi = {
    // 获取下载任务列表
    getTasks(params: {
        page: number
        pageSize: number
        name?: string
        status?: number
    }) {
        return api.get<PageResponse>('/download/task/list', {
            params
        })
    },

    // 更新任务状态
    updateTask(id: string, data: { status: number }) {
        return api.put<Response>(`/download/task/${id}`, data)
    },

    // 删除任务
    deleteTask(id: string) {
        return api.delete<Response>(`/download/task/${id}`)
    }
} 