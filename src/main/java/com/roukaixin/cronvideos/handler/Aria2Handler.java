package com.roukaixin.cronvideos.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class Aria2Handler extends TextWebSocketHandler {

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    private final Long id;

    private final Aria2WebSocketPool aria2WebSocketPool;

    public Aria2Handler(Aria2DownloadTasksMapper aria2DownloadTasksMapper, Long id,
                        Aria2WebSocketPool aria2WebSocketPool) {
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
        this.id = id;
        this.aria2WebSocketPool = aria2WebSocketPool;
    }

    /**
     * 建立连接后
     *
     * @param session session
     * @throws Exception ex
     */
    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws Exception {
        log.info("连接成功 {}", session.getId());
        aria2WebSocketPool.putSession(session, id);
        super.afterConnectionEstablished(session);
    }

    /**
     * 处理文本信息
     *
     * @param session session
     * @param message 返回消息
     * @throws Exception 异常
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        log.info("处理信息 {}", session.getId());
        log.info("aria2 返回消息: {}", message.getPayload());
        JSONObject aria2Repose = JSON.parseObject(message.getPayload());
        String method = aria2Repose.getString("method");
        if (method != null) {
            JSONArray params = aria2Repose.getJSONArray("params");
            String gid = params.getJSONObject(0).getString("gid");
            if (method.equals("aria2.onDownloadStart")) {
                // 任务开始下载,修改状态
                updateAria2TaskStatus(aria2WebSocketPool.getAria2ServiceId(session), 1, gid);
            }
            if (method.equals("aria2.onDownloadComplete")) {
                // 任务下载完成,修改状态
                updateAria2TaskStatus(aria2WebSocketPool.getAria2ServiceId(session), 2, gid);
            }
            if (method.equals("aria2.onDownloadError")) {
                // 重新处理下载失败的任务
                updateAria2TaskStatus(aria2WebSocketPool.getAria2ServiceId(session), 3, gid);
            }
        }
        super.handleTextMessage(session, message);
    }

    /**
     * 连接后关闭
     *
     * @param session session
     * @param status  status
     * @throws Exception ex
     */
    @Override
    public void afterConnectionClosed(@Nonnull WebSocketSession session, @Nonnull CloseStatus status) throws Exception {
        log.info("关闭连接 {}", session.getId());
        aria2WebSocketPool.removeSession(session);
        super.afterConnectionClosed(session, status);
    }

    private void updateAria2TaskStatus(Long aria2ServiceId, Integer status, String gid) {
        aria2DownloadTasksMapper.update(Wrappers.<Aria2DownloadTask>lambdaUpdate()
                .set(Aria2DownloadTask::getStatus, status).eq(Aria2DownloadTask::getGid, gid).eq(Aria2DownloadTask::getAria2ServiceId, aria2ServiceId));
    }

}
