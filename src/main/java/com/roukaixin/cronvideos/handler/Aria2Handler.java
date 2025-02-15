package com.roukaixin.cronvideos.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.Aria2ServerMapper;
import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import com.roukaixin.cronvideos.pojo.Aria2Server;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.*;

@Slf4j
public class Aria2Handler extends TextWebSocketHandler {

    @Getter
    private final CompletableFuture<Boolean> connectionFuture = new CompletableFuture<>();

    private ScheduledExecutorService scheduler;

    private final Long id;

    private final Integer weight;

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    private final Aria2WebSocketPool aria2WebSocketPool;

    private final Aria2ServerMapper aria2ServerMapper;

    private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;

    public Aria2Handler(Long id,
                        Integer weight,
                        Aria2DownloadTasksMapper aria2DownloadTasksMapper,
                        Aria2WebSocketPool aria2WebSocketPool,
                        Aria2ServerMapper aria2ServerMapper,
                        SmoothWeightedRoundRobin smoothWeightedRoundRobin) {
        this.id = id;
        this.weight = weight;
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
        this.aria2WebSocketPool = aria2WebSocketPool;
        this.aria2ServerMapper = aria2ServerMapper;
        this.smoothWeightedRoundRobin = smoothWeightedRoundRobin;
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
        smoothWeightedRoundRobin.put(id, weight);
        connectionFuture.complete(true);
        cancelTimeout();
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
        log.info("处理信息 {} {}", session.getId(), id);
        log.info("aria2 返回消息: {}", message.getPayload());
        JSONObject aria2Repose = JSON.parseObject(message.getPayload());
        String method = aria2Repose.getString("method");
        if (method != null) {
            JSONArray params = aria2Repose.getJSONArray("params");
            String gid = params.getJSONObject(0).getString("gid");
            if (method.equals("aria2.onDownloadStart")) {
                // 任务开始下载,修改状态
                updateAria2TaskStatus(id, 1, gid);
            }
            if (method.equals("aria2.onDownloadComplete")) {
                // 任务下载完成,修改状态
                updateAria2TaskStatus(id, 2, gid);
            }
            if (method.equals("aria2.onDownloadError")) {
                // 重新处理下载失败的任务
                updateAria2TaskStatus(id, 3, gid);
            }
        }
        super.handleTextMessage(session, message);
    }

    @Override
    public void handleTransportError(@Nonnull WebSocketSession session, Throwable exception) throws Exception {
        log.info("连接失败 {}", exception.getMessage());
        super.handleTransportError(session, exception);
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
        log.info("关闭连接 {} {} {}", session.getId(), status, id);
        // 手动停止 CloseStatus[code=1000, reason=null]
        if (status.getCode() == 1006) {
            // 强制关闭 CloseStatus[code=1006, reason=null]
            Aria2Server aria2Server = new Aria2Server();
            aria2Server.setId(id);
            aria2Server.setIsOnline(0);
            aria2ServerMapper.updateById(aria2Server);
        }
        aria2WebSocketPool.removeSession(session);
        aria2WebSocketPool.remove(id);
        // 重置id
        smoothWeightedRoundRobin.remove(id);
        super.afterConnectionClosed(session, status);
    }

    private void updateAria2TaskStatus(Long aria2ServiceId, Integer status, String gid) {
        aria2DownloadTasksMapper.update(Wrappers.<Aria2DownloadTask>lambdaUpdate()
                .set(Aria2DownloadTask::getStatus, status).eq(Aria2DownloadTask::getGid, gid).eq(Aria2DownloadTask::getAria2ServiceId, aria2ServiceId));
    }

    public void setTimeout(long timeout, TimeUnit unit) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            if (!connectionFuture.isDone()) {
                connectionFuture.completeExceptionally(new TimeoutException("Connection timed out"));
            }
        }, timeout, unit);
    }

    private void cancelTimeout() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

}
