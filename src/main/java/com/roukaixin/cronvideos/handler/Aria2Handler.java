package com.roukaixin.cronvideos.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class Aria2Handler extends TextWebSocketHandler {

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    public Aria2Handler(Aria2DownloadTasksMapper aria2DownloadTasksMapper) {
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
    }

    /**
     * 处理文本信息
     * @param session session
     * @param message 返回消息
     * @throws Exception 异常
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        log.info("aria2 返回消息: {}", message.getPayload());
        JSONObject aria2Repose = JSON.parseObject(message.getPayload());
        String method = aria2Repose.getString("method");
        if (method != null) {
            JSONArray params = aria2Repose.getJSONArray("params");
            String gid = params.getJSONObject(0).getString("gid");
            if (method.equals("aria2.onDownloadStart")) {
                // 任务开始下载,修改状态
                updateAria2TaskStatus(1, gid);
            }
            if (method.equals("aria2.onDownloadComplete")) {
                // 任务下载完成,修改状态
                updateAria2TaskStatus(2, gid);
            }
            if (method.equals("aria2.onDownloadError")) {
                // 重新处理下载失败的任务
            }
        }
        super.handleTextMessage(session, message);
    }

    private void updateAria2TaskStatus(Integer status, String gid) {
        aria2DownloadTasksMapper.update(Wrappers.<Aria2DownloadTask>lambdaUpdate()
                .set(Aria2DownloadTask::getStatus, status).eq(Aria2DownloadTask::getGid, gid));
    }
}
