import { api } from "@/api";
export const aria2DownloadTaskApi = {
    // 获取下载任务列表
    getTasks(params) {
        return api.get('/download/task/list', {
            params
        });
    },
    // 更新任务状态
    updateTask(id, data) {
        return api.put(`/download/task/${id}`, data);
    },
    // 删除任务
    deleteTask(id) {
        return api.delete(`/download/task/${id}`);
    }
};
